package rw.twaza.umushoferi.mode;

public class User {
    private  String email,password,fname,lname,phone,gender,avatarUri;


    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getFname() {
        return fname;
    }

    public void setFname(String fname) {
        this.fname = fname;
    }

    public String getLname() {
        return lname;
    }

    public void setLname(String lname) {
        this.lname = lname;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getGender() {
        return gender;
    }

    public void setGender(String gender) {
        this.gender = gender;
    }

    public User()
    {


    }
  public User(String email, String password, String fname, String lname, String phone, String gender) {
        this.email = email;
        this.password = password;
        this.fname = fname;
        this.lname = lname;
        this.phone = phone;
        this.gender = gender;
    }

    public String getAvatarUri() {
        return avatarUri;
    }



}


