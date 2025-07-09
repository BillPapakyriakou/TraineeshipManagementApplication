package traineeship_app.domainmodel;


import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;

@Entity
@Table(name = "committee_member")
public class Committee extends User{

    @Column(name = "committee_member_name", nullable = false)
    private String committeeMemberName;

    public Committee(String committeeMemberName){
        this.committeeMemberName = committeeMemberName;
    }


    public Committee(){

    }


    public String getCommitteeMemberName(){
        return committeeMemberName;
    }

    public void setCommitteeMemberName(String committeeMemberName){
        this.committeeMemberName = committeeMemberName;
    }
}
