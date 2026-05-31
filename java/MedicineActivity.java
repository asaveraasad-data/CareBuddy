// ═══════════════════════════════════════════════════════════
// FILE: MedicineActivity.java
// ═══════════════════════════════════════════════════════════
package com.carebuddy;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import java.util.Calendar;
import java.util.List;

public class MedicineActivity extends AppCompatActivity {

    private RecyclerView recyclerView;
    private MedicineAdapter adapter;
    private List<Medicine> medicineList;
    private DatabaseHelper db;
    private SessionManager session;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_medicine);

        db      = new DatabaseHelper(this);
        session = new SessionManager(this);

        recyclerView = findViewById(R.id.recycler_medicines);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        Button btnAdd = findViewById(R.id.btn_add_medicine);
        btnAdd.setOnClickListener(v ->
            startActivity(new Intent(this, AddMedicineActivity.class)));

        // Back button
        findViewById(R.id.btn_back).setOnClickListener(v -> finish());

        loadMedicines();
    }

    @Override
    protected void onResume() {
        super.onResume();
        loadMedicines();
    }

    private void loadMedicines() {
        medicineList = db.getMedicines(session.getEmail());
        adapter = new MedicineAdapter(this, medicineList,
            // Delete callback
            medicine -> {
                db.deleteMedicine(medicine.getId());
                cancelAlarm(medicine);
                Toast.makeText(this, medicine.getName() + " deleted", Toast.LENGTH_SHORT).show();
                loadMedicines();
            },
            // Edit callback
            medicine -> {
                Intent intent = new Intent(this, AddMedicineActivity.class);
                intent.putExtra("medicine_id", medicine.getId());
                startActivity(intent);
            });
        recyclerView.setAdapter(adapter);
    }

    // ── AlarmManager: schedule reminder ──────────────────────────────────────
    public static void scheduleAlarm(Context ctx, Medicine med) {
        if (!med.isReminderOn()) return;

        AlarmManager am = (AlarmManager) ctx.getSystemService(Context.ALARM_SERVICE);
        Intent intent   = new Intent(ctx, MedicineReminderReceiver.class);
        intent.putExtra("med_name",   med.getName());
        intent.putExtra("med_dosage", med.getDosage());

        PendingIntent pi = PendingIntent.getBroadcast(
            ctx, med.getId(), intent,
            PendingIntent.FLAG_UPDATE_CURRENT | PendingIntent.FLAG_IMMUTABLE);

        Calendar cal = Calendar.getInstance();
        switch (med.getTime()) {
            case "Morning":   cal.set(Calendar.HOUR_OF_DAY,  8); break;
            case "Afternoon": cal.set(Calendar.HOUR_OF_DAY, 14); break;
            case "Evening":   cal.set(Calendar.HOUR_OF_DAY, 18); break;
            case "Night":     cal.set(Calendar.HOUR_OF_DAY, 22); break;
        }
        cal.set(Calendar.MINUTE, 0);
        cal.set(Calendar.SECOND, 0);

        if (cal.getTimeInMillis() < System.currentTimeMillis()) {
            cal.add(Calendar.DAY_OF_MONTH, 1);
        }

        if (am != null) {
            am.setRepeating(AlarmManager.RTC_WAKEUP, cal.getTimeInMillis(),
                AlarmManager.INTERVAL_DAY, pi);
        }
    }

    public static void cancelAlarm(Medicine med) { /* cancel logic via context if needed */ }
    public void cancelAlarm(Context ctx, Medicine med) {
        AlarmManager am = (AlarmManager) ctx.getSystemService(Context.ALARM_SERVICE);
        Intent intent   = new Intent(ctx, MedicineReminderReceiver.class);
        PendingIntent pi = PendingIntent.getBroadcast(
            ctx, med.getId(), intent,
            PendingIntent.FLAG_UPDATE_CURRENT | PendingIntent.FLAG_IMMUTABLE);
        if (am != null) am.cancel(pi);
    }
}


// ═══════════════════════════════════════════════════════════
// FILE: AddMedicineActivity.java
// ═══════════════════════════════════════════════════════════
package com.carebuddy;

import android.app.DatePickerDialog;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.*;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;
import java.util.Calendar;

public class AddMedicineActivity extends AppCompatActivity {

    private EditText etName, etDosage, etStartDate, etEndDate;
    private String selectedTime = "Morning";
    private String selectedRepeat = "Every Day";
    private Switch switchReminder;
    private DatabaseHelper db;
    private SessionManager session;
    private int editMedId = -1;

    private CardView cardMorning, cardAfternoon, cardEvening, cardNight;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_medicine);

        db      = new DatabaseHelper(this);
        session = new SessionManager(this);

        etName        = findViewById(R.id.et_medicine_name);
        etDosage      = findViewById(R.id.et_dosage);
        etStartDate   = findViewById(R.id.et_start_date);
        etEndDate     = findViewById(R.id.et_end_date);
        switchReminder = findViewById(R.id.switch_reminder);
        Button btnSave = findViewById(R.id.btn_save_medicine);

        cardMorning   = findViewById(R.id.card_morning);
        cardAfternoon = findViewById(R.id.card_afternoon);
        cardEvening   = findViewById(R.id.card_evening);
        cardNight     = findViewById(R.id.card_night);

        // Time selection
        cardMorning.setOnClickListener(v   -> selectTime("Morning",   cardMorning));
        cardAfternoon.setOnClickListener(v -> selectTime("Afternoon", cardAfternoon));
        cardEvening.setOnClickListener(v   -> selectTime("Evening",   cardEvening));
        cardNight.setOnClickListener(v     -> selectTime("Night",     cardNight));

        // Repeat selection
        Button btnEveryDay  = findViewById(R.id.btn_every_day);
        Button btnWeekly    = findViewById(R.id.btn_weekly);
        Button btnAltDays   = findViewById(R.id.btn_alt_days);
        Button btnCustom    = findViewById(R.id.btn_custom);
        btnEveryDay.setOnClickListener(v -> selectedRepeat = "Every Day");
        btnWeekly.setOnClickListener(v   -> selectedRepeat = "Weekly");
        btnAltDays.setOnClickListener(v  -> selectedRepeat = "Alternate Days");
        btnCustom.setOnClickListener(v   -> selectedRepeat = "Custom");

        // Date pickers
        etStartDate.setOnClickListener(v -> showDatePicker(etStartDate));
        etEndDate.setOnClickListener(v   -> showDatePicker(etEndDate));

        // Check if editing
        editMedId = getIntent().getIntExtra("medicine_id", -1);
        // (Pre-fill would require fetching medicine by ID from DB — optional enhancement)

        btnSave.setOnClickListener(v -> saveMedicine());

        findViewById(R.id.btn_back).setOnClickListener(v -> finish());
        findViewById(R.id.btn_cancel).setOnClickListener(v -> finish());
    }

    private void selectTime(String time, CardView selected) {
        selectedTime = time;
        // Reset all card backgrounds
        int normal   = getColor(R.color.white);
        int active   = getColor(R.color.blue_light);
        cardMorning.setCardBackgroundColor(normal);
        cardAfternoon.setCardBackgroundColor(normal);
        cardEvening.setCardBackgroundColor(normal);
        cardNight.setCardBackgroundColor(normal);
        selected.setCardBackgroundColor(active);
    }

    private void showDatePicker(EditText target) {
        Calendar cal = Calendar.getInstance();
        new DatePickerDialog(this, (view, y, m, d) -> {
            target.setText(d + "/" + (m + 1) + "/" + y);
        }, cal.get(Calendar.YEAR), cal.get(Calendar.MONTH),
           cal.get(Calendar.DAY_OF_MONTH)).show();
    }

    private void saveMedicine() {
        String name  = etName.getText().toString().trim();
        String dose  = etDosage.getText().toString().trim();

        if (TextUtils.isEmpty(name)) { etName.setError("Required"); return; }
        if (TextUtils.isEmpty(dose)) { etDosage.setError("Required"); return; }

        Medicine med = new Medicine();
        med.setName(name);
        med.setDosage(dose);
        med.setTime(selectedTime);
        med.setRepeat(selectedRepeat);
        med.setStartDate(etStartDate.getText().toString());
        med.setEndDate(etEndDate.getText().toString());
        med.setReminderOn(switchReminder.isChecked());
        med.setUserEmail(session.getEmail());

        long id = db.addMedicine(med);
        if (id != -1) {
            med.setId((int) id);
            if (med.isReminderOn()) {
                MedicineActivity.scheduleAlarm(this, med);
            }
            Toast.makeText(this, "Medicine saved!", Toast.LENGTH_SHORT).show();
            finish();
        } else {
            Toast.makeText(this, "Error saving medicine", Toast.LENGTH_SHORT).show();
        }
    }
}
