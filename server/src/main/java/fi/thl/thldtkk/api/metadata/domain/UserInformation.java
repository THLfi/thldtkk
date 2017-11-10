package fi.thl.thldtkk.api.metadata.domain;

public class UserInformation extends UserProfile{

  public UserInformation(UserProfile profile){
    if (profile != null){
      profile.getFirstName().ifPresent(item -> super.setFirstName(item));
      profile.getLastName().ifPresent(item -> super.setLastName(item));
    }
  }
}
