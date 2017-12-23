package sec.project.controller;

import java.io.FileReader;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.List;
import org.h2.tools.RunScript;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import sec.project.domain.Signup;
import sec.project.repository.SignupRepository;

@Controller
public class SignupController {
    
    //These are needed only for sql injection
    Connection connection = null;
    private String databaseAddress = "jdbc:h2:file:./database";

    @Autowired
    private SignupRepository signupRepository;

    @RequestMapping("*")
    public String defaultMapping() {
        return "redirect:/form";
    }

    @RequestMapping(value = "/form", method = RequestMethod.GET)
    public String loadForm() {
        return "form";
    }

    @RequestMapping(value = "/form", method = RequestMethod.POST)
    public String submitForm(@RequestParam String name, @RequestParam String message, Model model) {
        addSignupToDb(name, message);
        List<Signup> allSignups = readSignupsFromDb();
        //signupRepository.save(new Signup(name, message));
        //List<Signup> allSignups = signupRepository.findAll();
        model.addAttribute("signups", allSignups);
        return "done";
    }
    
    public void addSignupToDb(String attendee, String message) {
        try {
            connection = DriverManager.getConnection("jdbc:h2:mem:db1", "sa", "");
            RunScript.execute(connection, new FileReader("src/main/resources/schema.sql"));
            //RunScript.execute(connection, new FileReader("src/main/resources/import.sql"));
            connection.createStatement().executeUpdate("INSERT INTO Signup (attendee, message) VALUES ('" +attendee+ "', '" +message+ "');");
            connection.close();
        } catch (Throwable t) {
            System.err.println("ERROR1: " + t.getMessage());
        }
         
    }
    
      public ArrayList<Signup> readSignupsFromDb() {
        try {
            ArrayList<Signup> fromDB = new ArrayList<>();;
            connection = DriverManager.getConnection("jdbc:h2:mem:db1", "sa", "");
            ResultSet resultSet = connection.createStatement().executeQuery("SELECT * FROM Signup");
            while (resultSet.next()) {
                Long id = resultSet.getLong("id");
                String name = resultSet.getString("attendee");
                String message = resultSet.getString("message");
                Signup current = new Signup(name, message, id);
                fromDB.add(current); 
            }
            connection.close();
            return fromDB;
        } catch (Throwable t) {
            System.err.println("ERROR2: " + t.getMessage());
            return null;
        }
            
    }  
}
