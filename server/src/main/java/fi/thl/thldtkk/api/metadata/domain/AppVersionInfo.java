package fi.thl.thldtkk.api.metadata.domain;

public class AppVersionInfo {

  private final String gitBuildVersion;
  private final String gitCommitId;
  private final String gitDirty;

  public AppVersionInfo(String gitBuildVersion,
                        String gitCommitId,
                        String gitDirty) {

    this.gitBuildVersion = gitBuildVersion;
    this.gitCommitId = gitCommitId;
    this.gitDirty = gitDirty;
  }

  public String getGitBuildVersion() {
    return gitBuildVersion;
  }

  public String getGitCommitId() {
    return gitCommitId;
  }

  public String getGitDirty() {
    return gitDirty;
  }

}
