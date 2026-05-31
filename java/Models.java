// ═══════════════════════════════════════════════════════════
// FILE: User.java
// ═══════════════════════════════════════════════════════════
package com.carebuddy;

public class User {
    private String name, email, password, role, age, bloodGroup;
    private String emergencyName, emergencyPhone, conditions, allergies;

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }
    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }
    public String getPassword() { return password; }
    public void setPassword(String password) { this.password = password; }
    public String getRole() { return role; }
    public void setRole(String role) { this.role = role; }
    public String getAge() { return age; }
    public void setAge(String age) { this.age = age; }
    public String getBloodGroup() { return bloodGroup; }
    public void setBloodGroup(String bloodGroup) { this.bloodGroup = bloodGroup; }
    public String getEmergencyName() { return emergencyName; }
    public void setEmergencyName(String emergencyName) { this.emergencyName = emergencyName; }
    public String getEmergencyPhone() { return emergencyPhone; }
    public void setEmergencyPhone(String emergencyPhone) { this.emergencyPhone = emergencyPhone; }
    public String getConditions() { return conditions; }
    public void setConditions(String conditions) { this.conditions = conditions; }
    public String getAllergies() { return allergies; }
    public void setAllergies(String allergies) { this.allergies = allergies; }
}


// ═══════════════════════════════════════════════════════════
// FILE: Medicine.java
// ═══════════════════════════════════════════════════════════
package com.carebuddy;

public class Medicine {
    private int id;
    private String name, dosage, time, repeat, startDate, endDate, userEmail;
    private boolean reminderOn;

    public int getId() { return id; }
    public void setId(int id) { this.id = id; }
    public String getName() { return name; }
    public void setName(String name) { this.name = name; }
    public String getDosage() { return dosage; }
    public void setDosage(String dosage) { this.dosage = dosage; }
    public String getTime() { return time; }
    public void setTime(String time) { this.time = time; }
    public String getRepeat() { return repeat; }
    public void setRepeat(String repeat) { this.repeat = repeat; }
    public String getStartDate() { return startDate; }
    public void setStartDate(String startDate) { this.startDate = startDate; }
    public String getEndDate() { return endDate; }
    public void setEndDate(String endDate) { this.endDate = endDate; }
    public String getUserEmail() { return userEmail; }
    public void setUserEmail(String userEmail) { this.userEmail = userEmail; }
    public boolean isReminderOn() { return reminderOn; }
    public void setReminderOn(boolean reminderOn) { this.reminderOn = reminderOn; }
}


// ═══════════════════════════════════════════════════════════
// FILE: HealthLog.java
// ═══════════════════════════════════════════════════════════
package com.carebuddy;

public class HealthLog {
    private int id;
    private String metric, value, date, userEmail;

    public int getId() { return id; }
    public void setId(int id) { this.id = id; }
    public String getMetric() { return metric; }
    public void setMetric(String metric) { this.metric = metric; }
    public String getValue() { return value; }
    public void setValue(String value) { this.value = value; }
    public String getDate() { return date; }
    public void setDate(String date) { this.date = date; }
    public String getUserEmail() { return userEmail; }
    public void setUserEmail(String userEmail) { this.userEmail = userEmail; }
}
