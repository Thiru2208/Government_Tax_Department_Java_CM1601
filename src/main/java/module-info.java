module com.iit.tutorials.taxdepartment {
    requires javafx.controls;
    requires javafx.fxml;
    requires org.json;


    opens com.iit.tutorials.taxdepartment to javafx.fxml;
    exports com.iit.tutorials.taxdepartment;
}