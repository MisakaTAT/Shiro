package com.mikuac.shiro.core;

import lombok.Getter;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.apache.maven.repository.internal.MavenRepositorySystemUtils;
import org.eclipse.aether.DefaultRepositorySystemSession;
import org.eclipse.aether.RepositorySystem;
import org.eclipse.aether.artifact.Artifact;
import org.eclipse.aether.artifact.DefaultArtifact;
import org.eclipse.aether.connector.basic.BasicRepositoryConnectorFactory;
import org.eclipse.aether.impl.DefaultServiceLocator;
import org.eclipse.aether.repository.LocalRepository;
import org.eclipse.aether.repository.RemoteRepository;
import org.eclipse.aether.resolution.ArtifactRequest;
import org.eclipse.aether.resolution.ArtifactResolutionException;
import org.eclipse.aether.resolution.ArtifactResult;
import org.eclipse.aether.spi.connector.RepositoryConnectorFactory;
import org.eclipse.aether.spi.connector.transport.TransporterFactory;
import org.eclipse.aether.transfer.TransferEvent;
import org.eclipse.aether.transfer.TransferListener;
import org.eclipse.aether.transport.file.FileTransporterFactory;
import org.eclipse.aether.transport.http.HttpTransporterFactory;
import com.mikuac.shiro.properties.ShiroProperties;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.io.File;
import java.nio.file.Paths;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;

@Slf4j
@Component
public class DependencyResolver {

    public static final File DEPENDENCIES_DIR = Paths.get("dependencies").toFile();

    private final RepositorySystem repositorySystem;
    private final DefaultRepositorySystemSession session;
    private final List<RemoteRepository> repositories;

    private final ConcurrentHashMap<String, Boolean> downloadingArtifacts = new ConcurrentHashMap<>();

    public DependencyResolver(ShiroProperties properties) {
        log.info("Initializing dependency resolver");
        this.repositorySystem = createRepositorySystem();
        this.session = MavenRepositorySystemUtils.newSession();
        LocalRepository localRepo = new LocalRepository(DEPENDENCIES_DIR);
        session.setLocalRepositoryManager(
                repositorySystem.newLocalRepositoryManager(session, localRepo));
        session.setTransferListener(new ImprovedTransferListener());
        
        String repoUrl = properties.getPluginMavenRepositoryUrl();
        if (repoUrl == null || repoUrl.isEmpty()) {
            repoUrl = "https://repo.maven.apache.org/maven2/";
        }

        RemoteRepository mavenRepo = new RemoteRepository.Builder(
                "central", "default", repoUrl).build();

        this.repositories = Collections.singletonList(mavenRepo);
    }

    @SuppressWarnings("deprecation")
    private static RepositorySystem createRepositorySystem() {
        DefaultServiceLocator locator = MavenRepositorySystemUtils.newServiceLocator();
        locator.addService(RepositoryConnectorFactory.class, BasicRepositoryConnectorFactory.class);
        locator.addService(TransporterFactory.class, FileTransporterFactory.class);
        locator.addService(TransporterFactory.class, HttpTransporterFactory.class);
        return locator.getService(RepositorySystem.class);
    }

    public void resolveDependency(String coordinates) throws ArtifactResolutionException {
        if (downloadingArtifacts.putIfAbsent(coordinates, Boolean.TRUE) != null) {
            log.debug("Dependency {} already in download queue, skipping", coordinates);
            return;
        }
        try {
            Artifact artifact = new DefaultArtifact(coordinates);
            ArtifactRequest request = new ArtifactRequest();
            request.setArtifact(artifact);
            request.setRepositories(repositories);
            log.debug("Starting to resolve artifact: {}", artifact);
            ArtifactResult result = repositorySystem.resolveArtifact(session, request);
            log.debug("Successfully resolved artifact: {} stored at: {}",
                    artifact, result.getArtifact().getFile().getName());
        } catch (ArtifactResolutionException e) {
            log.error("Dependency resolution failed: {}", e.getMessage());
            throw e;
        } finally {
            downloadingArtifacts.remove(coordinates);
        }
    }

    private static class ImprovedTransferListener implements TransferListener {
        private static final int PROGRESS_BAR_WIDTH = 50;
        private final ConcurrentHashMap<String, DownloadStatus> downloads = new ConcurrentHashMap<>();
        private static final int[] MILESTONE_PERCENTAGES = {5, 25, 50, 75, 90, 100};

        @Override
        public void transferInitiated(TransferEvent event) {
            String resourceName = event.getResource().getResourceName();
            if (resourceName.endsWith(".jar") || resourceName.endsWith(".pom")) {
                log.info("Started downloading: {}", resourceName);
                downloads.put(resourceName, new DownloadStatus());
            }
        }

        @Override
        public void transferStarted(TransferEvent event) {
            // No implementation needed
        }

        @Override
        public void transferProgressed(TransferEvent event) {
            String resourceName = event.getResource().getResourceName();
            DownloadStatus status = downloads.get(resourceName);
            if (status == null) return;

            long contentLength = event.getResource().getContentLength();
            long transferred = event.getTransferredBytes();

            if (contentLength <= 0) return;
            
            double progress = (double) transferred / contentLength;
            status.setProgress(progress);
            
            // Check if current progress is a key milestone
            boolean isKeyMilestone = checkMilestone(status, progress);
            
            if (isKeyMilestone) {
                String fileName = extractFileName(resourceName);
                String progressBar = createProgressBar(progress);
                String transferredStr = formatSize(transferred);
                String totalStr = formatSize(contentLength);
                
                status.setLastLoggedProgress(progress);
                logProgress(fileName, progressBar, progress, transferredStr, totalStr);
            }
        }
        

        private boolean checkMilestone(DownloadStatus status, double progress) {
            int currentPercentage = (int)(progress * 100);
            int previousPercentage = (int)(status.getLastLoggedProgress() * 100);
            if (previousPercentage < 100 && progress >= 0.999) {
                status.setProgress(1.0); // Force to exactly 100%
                return true;
            }
            for (int milestone : MILESTONE_PERCENTAGES) {
                if (previousPercentage < milestone && currentPercentage >= milestone) {
                    return true;
                }
            }
            return false;
        }
        
        private String extractFileName(String resourceName) {
            int lastSlash = resourceName.lastIndexOf('/');
            if (lastSlash >= 0 && lastSlash < resourceName.length() - 1) {
                return resourceName.substring(lastSlash + 1);
            }
            return resourceName;
        }
        
        private String createProgressBar(double progress) {
            int filledLength = (int) (progress * PROGRESS_BAR_WIDTH);
            String bar = "[" + "=".repeat(filledLength);
            if (filledLength < PROGRESS_BAR_WIDTH) {
                bar += ">" + " ".repeat(PROGRESS_BAR_WIDTH - filledLength - 1);
            }
            return bar + "]";
        }
        
        private void logProgress(String fileName, String progressBar, double progress,
                               String transferredStr, String totalStr) {
            log.info("Downloading: {} {} {}% ({}/{})",
                    truncateFileName(fileName),
                    progressBar,
                    String.format("%.2f", progress * 100),
                    transferredStr,
                    totalStr);
        }

        private String truncateFileName(String fileName) {
            if (fileName.length() <= 30) {
                return fileName;
            }
            return fileName.substring(0, 30 - 3) + "...";
        }

        private String formatSize(long bytes) {
            if (bytes < 1024) {
                return bytes + " B";
            } else if (bytes < 1024 * 1024) {
                return String.format("%.1f KB", bytes / 1024.0);
            } else {
                return String.format("%.1f MB", bytes / (1024.0 * 1024));
            }
        }

        @Override
        public void transferCorrupted(TransferEvent event) {
            String resourceName = event.getResource().getResourceName();
            log.error("Download corrupted: {}", resourceName);
            downloads.remove(resourceName);
        }

        @Override
        public void transferSucceeded(TransferEvent event) {
            String resourceName = event.getResource().getResourceName();
            DownloadStatus status = downloads.remove(resourceName);
            if (status != null) {
                long contentLength = event.getResource().getContentLength();
                String totalStr = formatSize(contentLength);
                
                log.info("Download completed: {} ({})", resourceName, totalStr);
            }
        }

        @Override
        public void transferFailed(TransferEvent event) {
            String resourceName = event.getResource().getResourceName();
            log.error("Download failed: {} - {}", resourceName, event.getException().getMessage());
            downloads.remove(resourceName);
        }

        @Getter
        @Setter
        private static class DownloadStatus {
            private double progress = 0;
            private double lastLoggedProgress = 0;
        }
    }

}