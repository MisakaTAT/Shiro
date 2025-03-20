package com.mikuac.shiro.core.plugin_loader;

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

import java.io.File;
import java.nio.file.Paths;
import java.util.Collections;

@Slf4j
public class DependencyResolver {

    public static final File dependenciesDir = Paths.get("dependencies").toFile();
    // public static final File
    // dependenciesDir=Paths.get(System.getProperty("user.home"), ".m2",
    // "repository").toFile()

    private final RepositorySystem repositorySystem;
    private final DefaultRepositorySystemSession session;
    private final RemoteRepository mavenCentral;

    public DependencyResolver() {
        log.info("dependencyResolver initiating");
        // 初始化 Repository System
        this.repositorySystem = newRepositorySystem();

        // 初始化 Session
        this.session = MavenRepositorySystemUtils.newSession();
        LocalRepository localRepo = new LocalRepository(
                dependenciesDir);
        session.setLocalRepositoryManager(
                repositorySystem.newLocalRepositoryManager(session, localRepo));
        // 添加 TransferListener 以显示进度条
        session.setTransferListener(new ConsoleTransferListener());
        // 配置 Maven 中央仓库
        this.mavenCentral = new RemoteRepository.Builder(
                "central",
                "default",
                "https://repo1.maven.org/maven2/").build();
    }

    private static RepositorySystem newRepositorySystem() {
        DefaultServiceLocator locator = MavenRepositorySystemUtils.newServiceLocator();
        locator.addService(RepositoryConnectorFactory.class, BasicRepositoryConnectorFactory.class);
        locator.addService(TransporterFactory.class, FileTransporterFactory.class);
        locator.addService(TransporterFactory.class, HttpTransporterFactory.class);
        return locator.getService(RepositorySystem.class);
    }

    public static void main(String[] args) throws Exception {
        DependencyResolver resolver = new DependencyResolver();
        resolver.resolveDependency("org.apache.commons:commons-lang3:3.12.0");
    }

    public ArtifactResult resolveDependency(String coordinates) throws ArtifactResolutionException {
        Artifact artifact = new DefaultArtifact(coordinates);
        ArtifactRequest request = new ArtifactRequest();
        request.setArtifact(artifact);
        request.setRepositories(Collections.singletonList(mavenCentral));

        log.info("parsing artifact {} 从 {}", artifact, mavenCentral);

        try {
            ArtifactResult result = repositorySystem.resolveArtifact(session, request);
            log.info("successfully parsed artifact {} to file {} from repository {}",
                    artifact, result.getArtifact().getFile(), result.getRepository());
            return result;
        } catch (ArtifactResolutionException e) {
            log.error("parsing artifact failed: {}", e.getMessage());
            throw e;
        }
    }

    // 自定义 TransferListener 实现类
    private static class ConsoleTransferListener implements TransferListener {

        private static final int PROGRESS_BAR_WIDTH = 50;

        @Override
        public void transferInitiated(TransferEvent event) {
            log.info("开始下载: {}", event.getResource().getResourceName());
        }

        @Override
        public void transferStarted(TransferEvent event) {
            // 不需要实现
        }

        @Override
        public void transferProgressed(TransferEvent event) {
            long contentLength = event.getResource().getContentLength();
            long transferred = event.getTransferredBytes();
            if (contentLength > 0) {
                double progress = (double) transferred / contentLength;
                int filledLength = (int) (progress * PROGRESS_BAR_WIDTH);
                String progressBar = "[" + "=".repeat(filledLength) + " "
                        + " ".repeat(PROGRESS_BAR_WIDTH - filledLength) + "]";
                System.out.printf("\r下载进度: %s %.2f%%", progressBar, progress * 100);
            }
        }

        @Override
        public void transferCorrupted(TransferEvent event) {
            log.error("传输损坏: {}", event.getException().getMessage());
        }

        @Override
        public void transferSucceeded(TransferEvent event) {
            System.out.println(); // 换行
            log.info("下载完成: {}", event.getResource().getResourceName());
        }

        @Override
        public void transferFailed(TransferEvent event) {
            log.error("下载失败: {}", event.getException().getMessage());
        }
    }
}