package de.gtarc.server.util;

public class UserConfig {
    private  String username;
    private String password;
    private String UUID;
    private String name;
    private String surname;

    public UserConfig(){}
    public UserConfig(String uuid, String username, String password){
        this.username = username;
        this.password = password;
        this.UUID = uuid;
    }

    public String getUsername() {
        return username;
    }

    public UserConfig setUsername(String username) {
        this.username = username;return this;
    }

    public String getPassword() {
        return password;
    }

    public UserConfig setPassword(String password) {
        this.password = password;return this;
    }

    public String getUUID() {
        return UUID;
    }

    public UserConfig setUUID(String UUID) {
        this.UUID = UUID;return this;
    }
    public UserConfig setName(String name){
        this.name = name; return this;
    }
    public UserConfig setSurname(String surname){
        this.surname = surname; return this;
    }
    public String getName(){
        return name;
    }
    public String getSurname(){
        return surname;
    }

    @Override
    public String toString() {
        return "UserConfig{" +
                "username='" + username + '\'' +
                ", password='" + password + '\'' +
                ", UUID='" + UUID + '\'' +
                ", name='" + name + '\'' +
                ", surname='" + surname + '\'' +
                '}';
    }
}
