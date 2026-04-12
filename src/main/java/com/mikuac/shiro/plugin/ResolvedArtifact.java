package com.mikuac.shiro.plugin;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.io.File;

@AllArgsConstructor
@Data
public class ResolvedArtifact {
    File file;
    int depth; // 层级（关键）
}