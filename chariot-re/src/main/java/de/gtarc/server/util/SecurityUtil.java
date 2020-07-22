package de.gtarc.server.util;

import java.io.*;
import java.net.URISyntaxException;
import java.net.URL;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

/**
 * 
 * @author cemakpolat
 *
 */
public class SecurityUtil {

	public String configFile ="configs/users";
	List<UserConfig> users = new ArrayList<>();

	public SecurityUtil() {
		readConfigFile();
	}

	@Override
	public String toString() {
		return "SecurityUtil{" +
				"configFile='" + configFile + '\'' +
				", users=" + users +
				'}';
	}
	public List<UserConfig> getUsers(){
		return users;
	}
	public void readConfigFile() {
		URL res = getClass().getClassLoader().getResource(configFile);

		File file = null;
		try {
			file = Paths.get(res.toURI()).toFile();
		} catch (URISyntaxException e) {
			e.printStackTrace();
		}
		String absolutePath = file.getAbsolutePath();

		try {
			FileReader input = new FileReader(absolutePath);
			BufferedReader bufRead = new BufferedReader(input);
			
            String line;
            line = bufRead.readLine();
            if(line != null && !line.contains("##") && !line.isEmpty()) {
        			handleLine(line);
            }
			// Read through file one line at time. Print line # and line
            while (line != null){
                //System.out.println(count+": "+line);
                line = bufRead.readLine();
                if(line != null && !line.contains("##") && !line.isEmpty()) {
                		handleLine(line);
                }
            }
            bufRead.close();
        }catch (ArrayIndexOutOfBoundsException e){
			System.out.println("Usage: java ReadFile filename\n");			

		}catch (IOException e){
            e.printStackTrace();
        }
	}
	public void handleLine(String value) {
		String[] values = value.split(",");
		users.add(new UserConfig().setUUID(values[0]).setUsername(values[1]).setPassword(values[2]).setName(values[3]).setSurname(values[4]));

	}
//	public static void main(String[] args) throws URISyntaxException {
//		SecurityUtil config = new SecurityUtil();
//
//		System.out.print(config.toString());
//	}

}
