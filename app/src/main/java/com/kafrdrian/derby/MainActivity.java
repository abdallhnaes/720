package com.kafrdrian.derby;

import android.app.Activity;
import android.os.Bundle;
import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.*;
import android.graphics.drawable.GradientDrawable;
import android.text.InputType;
import android.view.*;
import android.view.inputmethod.InputMethodManager;
import android.widget.*;
import java.text.SimpleDateFormat;
import java.util.*;

public class MainActivity extends Activity {
    private final int BG = Color.rgb(5, 14, 11);
    private final int PANEL = Color.rgb(8, 28, 31);
    private final int PANEL_2 = Color.rgb(10, 42, 42);
    private final int GOLD = Color.rgb(216, 180, 90);
    private final int RED = Color.rgb(190, 50, 42);

    private final String MAIN_MANAGER_PIN = "5135";
    private final String TEAM2_MANAGER_PIN = "2222";

    private SharedPreferences sp;
    private LinearLayout root;
    private int role = 0; // 0 user, 1 main manager, 2 team two manager

    private Team teamA = new Team("كفردريان",
            new String[]{"ابو الليث","حسونة","عبدالله","عبدالخالق","سلوم","جليبيب","ابو احمد","يوسف"},
            "مصطفى عثمان", "3-3-1");
    private Team teamB = new Team("الفريق الثاني",
            new String[]{"الحارس","مدافع 1","مدافع 2","مدافع 3","وسط 1","محور","وسط 2","مهاجم"},
            "بديل", "3-3-1");

    private String nextDate = "";
    private String nextTime = "";
    private String nextPlace = "";
    private String nextNote = "";
    private boolean hideTeamAForUsers = false;
    private boolean hideTeamBForUsers = false;

    @Override
    protected void onCreate(Bundle b) {
        super.onCreate(b);
        sp = getSharedPreferences("derby_kafrdrian_v8", MODE_PRIVATE);
        load();
        role = 0;
        showViewerHome();
    }

    private void load() {
        loadTeam(teamA, "a");
        loadTeam(teamB, "b");
        nextDate = sp.getString("next_date", "");
        nextTime = sp.getString("next_time", "");
        nextPlace = sp.getString("next_place", "");
        nextNote = sp.getString("next_note", "");
        hideTeamAForUsers = sp.getBoolean("hide_a_users", false);
        hideTeamBForUsers = sp.getBoolean("hide_b_users", false);
    }

    private void loadTeam(Team t, String k) {
        t.name = sp.getString(k + "_name", t.name);
        t.formation = sp.getString(k + "_form", t.formation);
        t.substitute = sp.getString(k + "_sub", t.substitute);
        for (int i=0;i<8;i++) {
            t.players[i] = sp.getString(k + "_p" + i, t.players[i]);
            t.x[i] = sp.getFloat(k + "_x" + i, defaultX(i));
            t.y[i] = sp.getFloat(k + "_y" + i, defaultY(i));
        }
    }

    private void saveTeam(Team t, String k) {
        SharedPreferences.Editor e = sp.edit();
        e.putString(k + "_name", t.name);
        e.putString(k + "_form", t.formation);
        e.putString(k + "_sub", t.substitute);
        for (int i=0;i<8;i++) {
            e.putString(k + "_p" + i, t.players[i]);
            e.putFloat(k + "_x" + i, t.x[i]);
            e.putFloat(k + "_y" + i, t.y[i]);
        }
        e.apply();
    }

    private void saveTeams() {
        saveTeam(teamA, "a");
        saveTeam(teamB, "b");
    }

    private void saveNextMatch() {
        sp.edit()
                .putString("next_date", nextDate)
                .putString("next_time", nextTime)
                .putString("next_place", nextPlace)
                .putString("next_note", nextNote)
                .putBoolean("hide_a_users", hideTeamAForUsers)
                .putBoolean("hide_b_users", hideTeamBForUsers)
                .apply();
    }

    private float defaultX(int i) {
        float[] xs = {.50f,.22f,.50f,.78f,.30f,.50f,.70f,.50f};
        return xs[i];
    }

    private float defaultY(int i) {
        float[] ys = {.91f,.74f,.74f,.74f,.50f,.58f,.50f,.28f};
        return ys[i];
    }

    private void base() {
        root = new LinearLayout(this);
        root.setOrientation(LinearLayout.VERTICAL);
        root.setBackgroundColor(BG);
        root.setPadding(dp(8), dp(8), dp(8), dp(8));
        setContentView(root);
    }

    private TextView text(String s, int size, int color, int style) {
        TextView t = new TextView(this);
        t.setText(s);
        t.setTextSize(size);
        t.setTextColor(color);
        t.setTypeface(Typeface.DEFAULT_BOLD, style);
        t.setGravity(Gravity.CENTER);
        t.setPadding(dp(8), dp(6), dp(8), dp(6));
        return t;
    }

    private TextView label(String s) {
        TextView l = text(s, 14, GOLD, Typeface.BOLD);
        l.setGravity(Gravity.RIGHT);
        return l;
    }

    private Button button(String s) {
        Button b = new Button(this);
        b.setText(s);
        b.setTextSize(13);
        b.setTextColor(Color.WHITE);
        b.setAllCaps(false);
        b.setBackground(round(PANEL_2, dp(14), GOLD, 1));
        return b;
    }

    private GradientDrawable round(int color, int radius, int strokeColor, int stroke) {
        GradientDrawable gd = new GradientDrawable();
        gd.setColor(color);
        gd.setCornerRadius(radius);
        if (stroke > 0) gd.setStroke(dp(stroke), strokeColor);
        return gd;
    }

    private EditText input(String value) {
        EditText e = new EditText(this);
        e.setText(value);
        e.setTextSize(16);
        e.setTextColor(Color.WHITE);
        e.setSingleLine(true);
        e.setGravity(Gravity.RIGHT | Gravity.CENTER_VERTICAL);
        e.setPadding(dp(12), 0, dp(12), 0);
        e.setInputType(InputType.TYPE_CLASS_TEXT);
        e.setBackground(round(Color.rgb(12, 38, 40), dp(12), Color.rgb(38, 76, 76), 1));
        return e;
    }

    private void header(String active) {
        LinearLayout top = new LinearLayout(this);
        top.setOrientation(LinearLayout.VERTICAL);
        top.setBackground(round(Color.rgb(3, 23, 20), dp(18), GOLD, 1));
        top.setPadding(dp(6), dp(6), dp(6), dp(6));

        String roleText = "واجهة المستخدمين";
        if (role == 1) roleText = "لوحة المدير الرئيسي";
        if (role == 2) roleText = "مدير الفريق الثاني";

        top.addView(text("ديربي كفردريان", 25, Color.WHITE, Typeface.BOLD));
        top.addView(text(roleText, 13, Color.LTGRAY, Typeface.BOLD));

        int[] stats = getStats();
        LinearLayout score = new LinearLayout(this);
        score.setOrientation(LinearLayout.HORIZONTAL);
        score.setGravity(Gravity.CENTER);
        score.setPadding(dp(4), dp(6), dp(4), dp(6));
        score.addView(scoreCard(teamA.name, String.valueOf(stats[0]), "فوز"), new LinearLayout.LayoutParams(0, dp(86), 1));
        score.addView(scoreCard("تعادل", String.valueOf(stats[1]), "مباراة"), new LinearLayout.LayoutParams(0, dp(86), 1));
        score.addView(scoreCard(teamB.name, String.valueOf(stats[2]), "فوز"), new LinearLayout.LayoutParams(0, dp(86), 1));
        top.addView(score);
        root.addView(top, new LinearLayout.LayoutParams(-1, -2));

        LinearLayout tabs = new LinearLayout(this);
        tabs.setOrientation(LinearLayout.HORIZONTAL);

        String[] tabsText;
        if (role == 1) {
            tabsText = new String[]{"الرئيسية","فريق 1","فريق 2","الموعد","النتيجة","السجل","خروج"};
        } else if (role == 2) {
            tabsText = new String[]{"الرئيسية","فريقي","الخصوصية","خروج"};
        } else {
            tabsText = new String[]{"الرئيسية","المباريات","مدير"};
        }

        for (String s: tabsText) {
            Button b = button(s.equals(active) ? "● " + s : s);
            tabs.addView(b, new LinearLayout.LayoutParams(0, dp(48), 1));
            if (s.equals("الرئيسية")) b.setOnClickListener(v -> {
                if (role == 1) showAdminHome();
                else if (role == 2) showTeam2ManagerHome();
                else showViewerHome();
            });
            if (s.equals("فريق 1")) b.setOnClickListener(v -> showTeamEditor(true));
            if (s.equals("فريق 2") || s.equals("فريقي")) b.setOnClickListener(v -> showTeamEditor(false));
            if (s.equals("الخصوصية")) b.setOnClickListener(v -> showTeam2Privacy());
            if (s.equals("الموعد")) b.setOnClickListener(v -> showNextMatchEditor());
            if (s.equals("النتيجة")) b.setOnClickListener(v -> showAddResult());
            if (s.equals("السجل") || s.equals("المباريات")) b.setOnClickListener(v -> { if (role == 1) showAdminHistory(); else showViewerMatches(); });
            if (s.equals("مدير")) b.setOnClickListener(v -> showManagerGate());
            if (s.equals("خروج")) b.setOnClickListener(v -> { role = 0; showViewerHome(); });
        }

        LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(-1, -2);
        lp.setMargins(0, dp(6), 0, dp(6));
        root.addView(tabs, lp);
    }

    private View scoreCard(String title, String num, String label) {
        LinearLayout card = new LinearLayout(this);
        card.setOrientation(LinearLayout.VERTICAL);
        card.setGravity(Gravity.CENTER);
        card.setBackground(round(Color.rgb(8, 31, 33), dp(14), Color.rgb(75, 63, 31), 1));
        card.addView(text(title, 13, Color.WHITE, Typeface.BOLD));
        card.addView(text(num, 24, GOLD, Typeface.BOLD));
        card.addView(text(label, 11, Color.LTGRAY, Typeface.BOLD));
        return card;
    }

    private int[] getStats() {
        String history = sp.getString("history", "").trim();
        int a=0,d=0,b=0;
        if (!history.isEmpty()) {
            String[] rows = history.split("\\n");
            for (String r: rows) {
                Match m = parseMatch(r);
                if (m == null) continue;
                if (m.ga > m.gb) a++;
                else if (m.gb > m.ga) b++;
                else d++;
            }
        }
        return new int[]{a,d,b};
    }

    private TextView sectionTitle(String s) {
        TextView t = text(s, 17, GOLD, Typeface.BOLD);
        t.setBackground(round(Color.rgb(7, 25, 28), dp(12), GOLD, 1));
        LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(-1, -2);
        lp.setMargins(0, dp(8), 0, dp(5));
        t.setLayoutParams(lp);
        return t;
    }

    private void showViewerHome() {
        role = 0;
        load();
        base();
        header("الرئيسية");

        ScrollView sv = new ScrollView(this);
        LinearLayout box = new LinearLayout(this);
        box.setOrientation(LinearLayout.VERTICAL);
        sv.addView(box);

        addNextMatchCard(box, false);

        box.addView(sectionTitle("الخطة الموضوعة للمباراة القادمة"));
        TacticsView a = new TacticsView(this);
        a.setTeam(teamA);
        box.addView(sectionTitle(teamA.name + " | " + teamA.formation + (hideTeamAForUsers ? " | مخفية للمستخدمين" : "")));
        box.addView(a, new LinearLayout.LayoutParams(-1, dp(620)));

        if (hideTeamBForUsers) {
            addHiddenTeamCard(box, teamB.name);
        } else {
            TacticsView b = new TacticsView(this);
            b.setTeam(teamB);
            box.addView(sectionTitle(teamB.name + " | " + teamB.formation));
            box.addView(b, new LinearLayout.LayoutParams(-1, dp(620)));
        }

        root.addView(sv, new LinearLayout.LayoutParams(-1, 0, 1));
    }

    private void showAdminHome() {
        role = 1;
        load();
        base();
        header("الرئيسية");

        ScrollView sv = new ScrollView(this);
        LinearLayout box = new LinearLayout(this);
        box.setOrientation(LinearLayout.VERTICAL);
        sv.addView(box);

        addNextMatchCard(box, true);

        box.addView(sectionTitle("الخطة الحالية التي يراها المستخدمون"));
        TacticsView a = new TacticsView(this);
        a.setTeam(teamA);
        box.addView(sectionTitle(teamA.name + " | " + teamA.formation));
        box.addView(a, new LinearLayout.LayoutParams(-1, dp(650)));

        TacticsView b = new TacticsView(this);
        b.setTeam(teamB);
        box.addView(sectionTitle(teamB.name + " | " + teamB.formation + (hideTeamBForUsers ? " | مخفية للمستخدمين" : "")));
        box.addView(b, new LinearLayout.LayoutParams(-1, dp(650)));

        root.addView(sv, new LinearLayout.LayoutParams(-1, 0, 1));
    }

    private void showTeam2ManagerHome() {
        role = 2;
        load();
        base();
        header("الرئيسية");

        ScrollView sv = new ScrollView(this);
        LinearLayout box = new LinearLayout(this);
        box.setOrientation(LinearLayout.VERTICAL);
        sv.addView(box);

        LinearLayout card = new LinearLayout(this);
        card.setOrientation(LinearLayout.VERTICAL);
        card.setBackground(round(PANEL, dp(16), GOLD, 1));
        card.setPadding(dp(8), dp(8), dp(8), dp(8));
        card.addView(text("صلاحياتك", 21, GOLD, Typeface.BOLD));
        card.addView(text("يمكنك تعديل كل ما يخص الفريق الثاني فقط، ويمكنك إخفاء تشكيلتك عن المستخدمين.", 15, Color.LTGRAY, Typeface.BOLD));
        box.addView(card);

        TacticsView b = new TacticsView(this);
        b.setTeam(teamB);
        box.addView(sectionTitle(teamB.name + " | " + teamB.formation));
        box.addView(b, new LinearLayout.LayoutParams(-1, dp(700)));

        root.addView(sv, new LinearLayout.LayoutParams(-1, 0, 1));
    }

    private void addHiddenTeamCard(LinearLayout box, String teamName) {
        LinearLayout card = new LinearLayout(this);
        card.setOrientation(LinearLayout.VERTICAL);
        card.setBackground(round(Color.rgb(35, 18, 18), dp(16), RED, 1));
        card.setPadding(dp(10), dp(15), dp(10), dp(15));
        card.addView(text(teamName, 20, Color.WHITE, Typeface.BOLD));
        card.addView(text("تم منع عرض تشكيلة هذا الفريق من قبل مدير الفريق الثاني", 16, Color.LTGRAY, Typeface.BOLD));
        LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(-1, -2);
        lp.setMargins(0, dp(8), 0, dp(8));
        box.addView(card, lp);
    }

    private void addNextMatchCard(LinearLayout box, boolean admin) {
        LinearLayout card = new LinearLayout(this);
        card.setOrientation(LinearLayout.VERTICAL);
        card.setBackground(round(PANEL, dp(16), GOLD, 1));
        card.setPadding(dp(8), dp(8), dp(8), dp(8));

        card.addView(text("موعد المباراة القادمة", 21, GOLD, Typeface.BOLD));

        String date = nextDate.trim().isEmpty() ? "لم يحدد بعد" : nextDate;
        String time = nextTime.trim().isEmpty() ? "" : " - الساعة: " + nextTime;
        String place = nextPlace.trim().isEmpty() ? "" : "المكان: " + nextPlace;
        String note = nextNote.trim().isEmpty() ? "" : "ملاحظة: " + nextNote;

        card.addView(text(teamA.name + "  ×  " + teamB.name, 19, Color.WHITE, Typeface.BOLD));
        card.addView(text(date + time, 17, Color.WHITE, Typeface.BOLD));
        if (!place.isEmpty()) card.addView(text(place, 15, Color.LTGRAY, Typeface.BOLD));
        if (!note.isEmpty()) card.addView(text(note, 14, Color.LTGRAY, Typeface.BOLD));

        if (admin) {
            Button edit = button("تعديل موعد المباراة القادمة");
            edit.setOnClickListener(v -> showNextMatchEditor());
            card.addView(edit, new LinearLayout.LayoutParams(-1, dp(52)));
        }

        LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(-1, -2);
        lp.setMargins(0, dp(5), 0, dp(10));
        box.addView(card, lp);
    }

    private void showManagerGate() {
        role = 0;
        base();

        root.addView(text("بوابة الإدارة", 29, Color.WHITE, Typeface.BOLD));
        root.addView(text("اختر نوع الحساب للدخول إلى لوحة التحكم", 15, Color.LTGRAY, Typeface.BOLD));

        LinearLayout card = new LinearLayout(this);
        card.setOrientation(LinearLayout.VERTICAL);
        card.setBackground(round(PANEL, dp(18), GOLD, 1));
        card.setPadding(dp(10), dp(10), dp(10), dp(10));

        Button main = button("مدير رئيسي");
        Button second = button("مدير الفريق الثاني");
        Button back = button("رجوع للمستخدمين");

        main.setTextSize(20);
        second.setTextSize(20);

        main.setOnClickListener(v -> showPinLogin(1));
        second.setOnClickListener(v -> showPinLogin(2));
        back.setOnClickListener(v -> showViewerHome());

        card.addView(main, new LinearLayout.LayoutParams(-1, dp(70)));
        addGap(card, 10);
        card.addView(second, new LinearLayout.LayoutParams(-1, dp(70)));
        addGap(card, 10);
        card.addView(back, new LinearLayout.LayoutParams(-1, dp(58)));

        LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(-1, -2);
        lp.setMargins(0, dp(22), 0, 0);
        root.addView(card, lp);

        root.addView(text("المدير الرئيسي يدير النظام كاملًا، ومدير الفريق الثاني يدير فريقه فقط مع إمكانية إخفاء تشكيلته.", 14, Color.LTGRAY, Typeface.BOLD));
    }

    private void showPinLogin(int requestedRole) {
        base();

        String title = requestedRole == 1 ? "دخول المدير الرئيسي" : "دخول مدير الفريق الثاني";
        root.addView(text(title, 26, Color.WHITE, Typeface.BOLD));
        root.addView(text("أدخل رمز المرور", 15, Color.LTGRAY, Typeface.BOLD));

        LinearLayout card = new LinearLayout(this);
        card.setOrientation(LinearLayout.VERTICAL);
        card.setBackground(round(PANEL, dp(18), GOLD, 1));
        card.setPadding(dp(10), dp(10), dp(10), dp(10));

        EditText pin = input("");
        pin.setHint("PIN");
        pin.setInputType(InputType.TYPE_CLASS_NUMBER | InputType.TYPE_NUMBER_VARIATION_PASSWORD);
        card.addView(pin, new LinearLayout.LayoutParams(-1, dp(58)));

        Button login = button("دخول");
        login.setOnClickListener(v -> {
            String code = pin.getText().toString().trim();
            if (requestedRole == 1 && MAIN_MANAGER_PIN.equals(code)) {
                role = 1;
                hideKeyboard();
                showAdminHome();
            } else if (requestedRole == 2 && TEAM2_MANAGER_PIN.equals(code)) {
                role = 2;
                hideKeyboard();
                showTeam2ManagerHome();
            } else {
                Toast.makeText(this, "الرمز غير صحيح", Toast.LENGTH_SHORT).show();
            }
        });
        card.addView(login, new LinearLayout.LayoutParams(-1, dp(58)));

        Button back = button("رجوع");
        back.setOnClickListener(v -> showManagerGate());
        card.addView(back, new LinearLayout.LayoutParams(-1, dp(54)));

        LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(-1, -2);
        lp.setMargins(0, dp(18), 0, 0);
        root.addView(card, lp);

        root.addView(text("أدخل رمز الدخول المخصص لهذا الحساب", 13, Color.LTGRAY, Typeface.BOLD));
    }

    private void showTeam2Privacy() {
        role = 2;
        load();
        base();
        header("الخصوصية");

        LinearLayout card = new LinearLayout(this);
        card.setOrientation(LinearLayout.VERTICAL);
        card.setBackground(round(PANEL, dp(16), GOLD, 1));
        card.setPadding(dp(10), dp(10), dp(10), dp(10));

        card.addView(text("خصوصية تشكيلة الفريق الثاني", 21, GOLD, Typeface.BOLD));
        card.addView(text("عند تفعيل الخيار، المستخدمون يرون النتيجة واسم الفريق فقط، ولا تظهر لهم تشكيلة الفريق الثاني.", 15, Color.LTGRAY, Typeface.BOLD));

        CheckBox hide = new CheckBox(this);
        hide.setText("منع عرض تشكيلة فريقي للمستخدمين");
        hide.setTextColor(Color.WHITE);
        hide.setTextSize(17);
        hide.setGravity(Gravity.RIGHT);
        hide.setChecked(hideTeamBForUsers);
        card.addView(hide, new LinearLayout.LayoutParams(-1, dp(60)));

        Button save = button("حفظ الخصوصية");
        save.setOnClickListener(v -> {
            hideTeamBForUsers = hide.isChecked();
            saveNextMatch();
            Toast.makeText(this, hideTeamBForUsers ? "تم إخفاء التشكيلة عن المستخدمين" : "تم السماح بعرض التشكيلة", Toast.LENGTH_SHORT).show();
            showTeam2ManagerHome();
        });
        card.addView(save, new LinearLayout.LayoutParams(-1, dp(58)));

        root.addView(card, new LinearLayout.LayoutParams(-1, -2));
    }

    private void showNextMatchEditor() {
        role = 1;
        load();
        base();
        header("الموعد");

        ScrollView sv = new ScrollView(this);
        LinearLayout box = new LinearLayout(this);
        box.setOrientation(LinearLayout.VERTICAL);
        box.setPadding(dp(4), dp(4), dp(4), dp(24));
        sv.addView(box);

        box.addView(sectionTitle("تحديد موعد المباراة القادمة"));
        box.addView(text(teamA.name + "  ×  " + teamB.name, 21, Color.WHITE, Typeface.BOLD));

        box.addView(label("تاريخ المباراة"));
        EditText date = input(nextDate.trim().isEmpty() ? new SimpleDateFormat("yyyy/MM/dd", Locale.US).format(new Date()) : nextDate);
        box.addView(date, new LinearLayout.LayoutParams(-1, dp(52)));

        box.addView(label("الوقت"));
        EditText time = input(nextTime.trim().isEmpty() ? "20:00" : nextTime);
        box.addView(time, new LinearLayout.LayoutParams(-1, dp(52)));

        box.addView(label("الملعب / المكان"));
        EditText place = input(nextPlace);
        box.addView(place, new LinearLayout.LayoutParams(-1, dp(52)));

        box.addView(label("ملاحظة"));
        EditText note = input(nextNote);
        box.addView(note, new LinearLayout.LayoutParams(-1, dp(52)));

        box.addView(sectionTitle("الخطة الموضوعة التي ستظهر للمستخدم"));
        TacticsView a = new TacticsView(this);
        a.setTeam(teamA);
        box.addView(sectionTitle(teamA.name + " | " + teamA.formation + (hideTeamAForUsers ? " | مخفية للمستخدمين" : "")));
        box.addView(a, new LinearLayout.LayoutParams(-1, dp(560)));

        TacticsView b = new TacticsView(this);
        b.setTeam(teamB);
        box.addView(sectionTitle(teamB.name + " | " + teamB.formation + (hideTeamBForUsers ? " | مخفية للمستخدمين" : "")));
        box.addView(b, new LinearLayout.LayoutParams(-1, dp(560)));

        Button save = button("حفظ الموعد والخطة للمستخدمين");
        save.setOnClickListener(v -> {
            nextDate = clean(date.getText().toString(), "");
            nextTime = clean(time.getText().toString(), "");
            nextPlace = clean(place.getText().toString(), "");
            nextNote = clean(note.getText().toString(), "");
            saveNextMatch();
            hideKeyboard();
            Toast.makeText(this, "تم حفظ موعد المباراة القادمة", Toast.LENGTH_SHORT).show();
            showAdminHome();
        });
        box.addView(save, new LinearLayout.LayoutParams(-1, dp(60)));

        root.addView(sv, new LinearLayout.LayoutParams(-1, 0, 1));
    }

    private void showTeamEditor(boolean first) {
        load();
        if (role == 2 && first) {
            Toast.makeText(this, "لا تملك صلاحية تعديل الفريق الأول", Toast.LENGTH_SHORT).show();
            showTeam2ManagerHome();
            return;
        }

        base();
        header(first ? "فريق 1" : (role == 2 ? "فريقي" : "فريق 2"));

        Team team = first ? teamA : teamB;
        ScrollView sv = new ScrollView(this);
        LinearLayout box = new LinearLayout(this);
        box.setOrientation(LinearLayout.VERTICAL);
        box.setPadding(dp(4), dp(4), dp(4), dp(24));
        sv.addView(box);

        box.addView(sectionTitle(first ? "تنظيم الفريق الأول" : "تنظيم الفريق الثاني"));

        box.addView(label("اسم الفريق"));
        EditText teamName = input(team.name);
        box.addView(teamName, new LinearLayout.LayoutParams(-1, dp(52)));

        box.addView(label("الخطة"));
        Spinner spinner = new Spinner(this);
        String[] forms = {"3-3-1", "3-2-2", "2-3-2", "1-3-3", "خطة يدوية"};
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_dropdown_item, forms);
        spinner.setAdapter(adapter);
        int idx = 0;
        for (int i=0;i<forms.length;i++) if (forms[i].equals(team.formation)) idx=i;
        spinner.setSelection(idx);
        box.addView(spinner, new LinearLayout.LayoutParams(-1, dp(52)));

        EditText[] inputs = new EditText[8];
        for (int i=0;i<8;i++) {
            box.addView(label(i == 0 ? "الحارس" : "لاعب " + (i+1)));
            inputs[i] = input(team.players[i]);
            box.addView(inputs[i], new LinearLayout.LayoutParams(-1, dp(52)));
        }

        box.addView(label("البديل"));
        EditText sub = input(team.substitute);
        box.addView(sub, new LinearLayout.LayoutParams(-1, dp(52)));

        if (role == 1 || !first) {
            CheckBox hideBox = new CheckBox(this);
            if (first) {
                hideBox.setText("منع عرض تشكيلة الفريق الأول للمستخدمين");
                hideBox.setChecked(hideTeamAForUsers);
            } else {
                hideBox.setText("منع عرض تشكيلة الفريق الثاني للمستخدمين");
                hideBox.setChecked(hideTeamBForUsers);
            }
            hideBox.setTextColor(Color.WHITE);
            hideBox.setTextSize(16);
            hideBox.setGravity(Gravity.RIGHT);
            box.addView(hideBox, new LinearLayout.LayoutParams(-1, dp(60)));

            Button savePrivacyOnly = button("حفظ الخصوصية فقط");
            savePrivacyOnly.setOnClickListener(v -> {
                if (first) hideTeamAForUsers = hideBox.isChecked();
                else hideTeamBForUsers = hideBox.isChecked();
                saveNextMatch();
                Toast.makeText(this, "تم حفظ الخصوصية", Toast.LENGTH_SHORT).show();
            });
            box.addView(savePrivacyOnly, new LinearLayout.LayoutParams(-1, dp(54)));
        }

        Button save = button("حفظ الفريق");
        save.setOnClickListener(v -> {
            team.name = clean(teamName.getText().toString(), first ? "الفريق الأول" : "الفريق الثاني");
            String newForm = spinner.getSelectedItem().toString();
            boolean changed = !newForm.equals(team.formation);
            team.formation = newForm;
            for (int i=0;i<8;i++) team.players[i] = clean(inputs[i].getText().toString(), "لاعب " + (i+1));
            team.substitute = clean(sub.getText().toString(), "بديل");
            if (changed && !team.formation.equals("خطة يدوية")) applyFormationDefaults(team);
            if (role == 1 || !first) {
                // read privacy checkbox if it exists
                for (int i=0; i<box.getChildCount(); i++) {
                    View child = box.getChildAt(i);
                    if (child instanceof CheckBox) {
                        if (first) hideTeamAForUsers = ((CheckBox) child).isChecked();
                        else hideTeamBForUsers = ((CheckBox) child).isChecked();
                    }
                }
                saveNextMatch();
            }
            saveTeams();
            hideKeyboard();
            Toast.makeText(this, "تم حفظ الفريق", Toast.LENGTH_SHORT).show();
            if (role == 2) showTeam2ManagerHome(); else showAdminHome();
        });
        box.addView(save, new LinearLayout.LayoutParams(-1, dp(58)));

        Button custom = button("رسم الخطة يدوياً بالسحب");
        custom.setOnClickListener(v -> {
            team.name = clean(teamName.getText().toString(), first ? "الفريق الأول" : "الفريق الثاني");
            team.formation = "خطة يدوية";
            for (int i=0;i<8;i++) team.players[i] = clean(inputs[i].getText().toString(), "لاعب " + (i+1));
            team.substitute = clean(sub.getText().toString(), "بديل");
            saveTeams();
            hideKeyboard();
            showManualPlan(first);
        });
        box.addView(custom, new LinearLayout.LayoutParams(-1, dp(58)));

        root.addView(sv, new LinearLayout.LayoutParams(-1, 0, 1));
    }

    private void applyFormationDefaults(Team team) {
        float[][] arr = TacticsView.getFormation(team.formation);
        for (int i=0;i<8;i++) {
            team.x[i] = arr[i][0];
            team.y[i] = arr[i][1];
        }
    }

    private void showManualPlan(boolean first) {
        load();
        if (role == 2 && first) {
            Toast.makeText(this, "لا تملك صلاحية تعديل الفريق الأول", Toast.LENGTH_SHORT).show();
            showTeam2ManagerHome();
            return;
        }

        base();
        header(first ? "فريق 1" : (role == 2 ? "فريقي" : "فريق 2"));
        Team team = first ? teamA : teamB;

        root.addView(text("اسحب اللاعبين داخل الملعب ثم اضغط حفظ", 15, Color.WHITE, Typeface.BOLD));

        ManualTacticsView editor = new ManualTacticsView(this);
        editor.setTeam(team);
        root.addView(editor, new LinearLayout.LayoutParams(-1, 0, 1));

        LinearLayout actions = new LinearLayout(this);
        actions.setOrientation(LinearLayout.HORIZONTAL);

        Button reset = button("ترتيب 3-3-1");
        reset.setOnClickListener(v -> {
            float[][] arr = TacticsView.getFormation("3-3-1");
            for (int i=0;i<8;i++) {
                team.x[i] = arr[i][0];
                team.y[i] = arr[i][1];
            }
            editor.invalidate();
        });

        Button saveBtn = button("حفظ");
        saveBtn.setOnClickListener(v -> {
            team.formation = "خطة يدوية";
            saveTeams();
            Toast.makeText(this, "تم حفظ الخطة اليدوية", Toast.LENGTH_SHORT).show();
            if (role == 2) showTeam2ManagerHome(); else showAdminHome();
        });

        actions.addView(reset, new LinearLayout.LayoutParams(0, dp(56), 1));
        actions.addView(saveBtn, new LinearLayout.LayoutParams(0, dp(56), 1));
        root.addView(actions);
    }

    private void showAddResult() {
        role = 1;
        load();
        base();
        header("النتيجة");

        ScrollView sv = new ScrollView(this);
        LinearLayout box = new LinearLayout(this);
        box.setOrientation(LinearLayout.VERTICAL);
        box.setPadding(dp(4), dp(4), dp(4), dp(24));
        sv.addView(box);

        box.addView(sectionTitle("تسجيل نتيجة مباراة جديدة"));
        box.addView(text(teamA.name + "  ×  " + teamB.name, 22, Color.WHITE, Typeface.BOLD));

        EditText date = input(new SimpleDateFormat("yyyy/MM/dd", Locale.US).format(new Date()));
        EditText goalsA = input("0");
        EditText goalsB = input("0");
        goalsA.setInputType(InputType.TYPE_CLASS_NUMBER);
        goalsB.setInputType(InputType.TYPE_CLASS_NUMBER);

        box.addView(label("تاريخ المباراة"));
        box.addView(date, new LinearLayout.LayoutParams(-1, dp(52)));
        box.addView(label("أهداف " + teamA.name));
        box.addView(goalsA, new LinearLayout.LayoutParams(-1, dp(52)));
        box.addView(label("أهداف " + teamB.name));
        box.addView(goalsB, new LinearLayout.LayoutParams(-1, dp(52)));

        box.addView(sectionTitle("الخطة التي ستُحفظ مع هذه المباراة"));
        TacticsView v1 = new TacticsView(this);
        v1.setTeam(teamA);
        box.addView(sectionTitle(teamA.name + (hideTeamAForUsers ? " | ستظهر مخفية للمستخدمين" : "")));
        box.addView(v1, new LinearLayout.LayoutParams(-1, dp(560)));
        TacticsView v2 = new TacticsView(this);
        v2.setTeam(teamB);
        box.addView(sectionTitle(teamB.name + (hideTeamBForUsers ? " | ستظهر مخفية للمستخدمين" : "")));
        box.addView(v2, new LinearLayout.LayoutParams(-1, dp(560)));

        Button saveResult = button("حفظ المباراة للمستخدمين");
        saveResult.setOnClickListener(v -> {
            int a = parse(goalsA.getText().toString());
            int b = parse(goalsB.getText().toString());
            String record = serializeMatch(date.getText().toString().trim(), a, b, teamA, teamB, hideTeamAForUsers, hideTeamBForUsers);
            String old = sp.getString("history", "");
            sp.edit().putString("history", record + "\n" + old).apply();
            hideKeyboard();
            Toast.makeText(this, "تم نشر المباراة في قسم المستخدمين", Toast.LENGTH_SHORT).show();
            showAdminHistory();
        });
        box.addView(saveResult, new LinearLayout.LayoutParams(-1, dp(60)));

        root.addView(sv, new LinearLayout.LayoutParams(-1, 0, 1));
    }

    private void showAdminHistory() {
        role = 1;
        showHistory(true);
    }

    private void showViewerMatches() {
        role = 0;
        showHistory(false);
    }

    private void showHistory(boolean admin) {
        load();
        base();
        header(admin ? "السجل" : "المباريات");

        ScrollView sv = new ScrollView(this);
        LinearLayout box = new LinearLayout(this);
        box.setOrientation(LinearLayout.VERTICAL);
        box.setPadding(dp(4), dp(4), dp(4), dp(24));
        sv.addView(box);

        box.addView(sectionTitle(admin ? "كل المباريات المسجلة" : "المباريات السابقة والخطط"));

        String history = sp.getString("history", "").trim();
        if (history.isEmpty()) {
            box.addView(text("لا توجد مباريات مسجلة بعد", 17, Color.LTGRAY, Typeface.BOLD));
        } else {
            String[] rows = history.split("\\n");
            for (String r: rows) {
                Match m = parseMatch(r);
                if (m != null) addMatchCard(box, m, admin);
            }
        }

        if (admin) {
            Button clear = button("مسح كل المباريات");
            clear.setOnClickListener(v -> {
                sp.edit().remove("history").apply();
                showAdminHistory();
            });
            box.addView(clear, new LinearLayout.LayoutParams(-1, dp(58)));
        }

        root.addView(sv, new LinearLayout.LayoutParams(-1, 0, 1));
    }

    private void addMatchCard(LinearLayout box, Match m, boolean admin) {
        LinearLayout card = new LinearLayout(this);
        card.setOrientation(LinearLayout.VERTICAL);
        card.setBackground(round(PANEL, dp(14), GOLD, 1));
        card.setPadding(dp(8), dp(8), dp(8), dp(8));

        card.addView(text(m.date, 13, Color.LTGRAY, Typeface.BOLD));
        card.addView(text(m.a.name + "  ×  " + m.b.name, 18, Color.WHITE, Typeface.BOLD));
        card.addView(text(m.ga + "  -  " + m.gb, 30, GOLD, Typeface.BOLD));

        if (!admin && m.hideAForUsers) {
            addHiddenTeamCard(card, m.a.name);
        } else {
            TacticsView aView = new TacticsView(this);
            aView.setTeam(m.a);
            card.addView(sectionTitle("خطة " + m.a.name + " | " + m.a.formation + (m.hideAForUsers ? " | مخفية للمستخدمين" : "")));
            card.addView(aView, new LinearLayout.LayoutParams(-1, dp(560)));
        }

        if (!admin && m.hideBForUsers) {
            addHiddenTeamCard(card, m.b.name);
        } else {
            TacticsView bView = new TacticsView(this);
            bView.setTeam(m.b);
            card.addView(sectionTitle("خطة " + m.b.name + " | " + m.b.formation + (m.hideBForUsers ? " | مخفية للمستخدمين" : "")));
            card.addView(bView, new LinearLayout.LayoutParams(-1, dp(560)));
        }

        LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(-1, -2);
        lp.setMargins(0, dp(8), 0, dp(8));
        box.addView(card, lp);
    }

    private String serializeMatch(String date, int ga, int gb, Team a, Team b, boolean hideA, boolean hideB) {
        return safe(date) + "§" + ga + "§" + gb + "§" + serializeTeam(a) + "§" + serializeTeam(b) + "§" + hideA + "§" + hideB;
    }

    private String serializeTeam(Team t) {
        return safe(t.name) + "¤" + safe(t.formation) + "¤" + safe(t.substitute) + "¤" +
                join(t.players) + "¤" + joinFloats(t.x) + "¤" + joinFloats(t.y);
    }

    private Match parseMatch(String row) {
        try {
            String[] p = row.split("§");
            if (p.length < 5) return null;
            Match m = new Match();
            m.date = p[0];
            m.ga = Integer.parseInt(p[1]);
            m.gb = Integer.parseInt(p[2]);
            m.a = parseTeam(p[3]);
            m.b = parseTeam(p[4]);
            if (p.length > 6) {
                m.hideAForUsers = "true".equals(p[5]);
                m.hideBForUsers = "true".equals(p[6]);
            } else {
                m.hideAForUsers = false;
                m.hideBForUsers = p.length > 5 && "true".equals(p[5]);
            }
            return m;
        } catch(Exception e) {
            return null;
        }
    }

    private Team parseTeam(String s) {
        String[] p = s.split("¤");
        String name = p.length > 0 ? p[0] : "فريق";
        String form = p.length > 1 ? p[1] : "3-3-1";
        String sub = p.length > 2 ? p[2] : "بديل";
        String[] players = p.length > 3 ? p[3].split("~") : new String[]{"1","2","3","4","5","6","7","8"};
        if (players.length < 8) {
            String[] fixed = {"1","2","3","4","5","6","7","8"};
            for (int i=0;i<players.length;i++) fixed[i]=players[i];
            players = fixed;
        }
        Team t = new Team(name, players, sub, form);
        if (p.length > 5) {
            float[] xs = parseFloats(p[4]);
            float[] ys = parseFloats(p[5]);
            for (int i=0;i<8;i++) {
                t.x[i] = xs[i];
                t.y[i] = ys[i];
            }
        }
        return t;
    }

    private String safe(String s) {
        if (s == null) return "";
        return s.replace("§"," ").replace("¤"," ").replace("~"," ").replace("\n"," ");
    }

    private String join(String[] arr) {
        String out = "";
        for (int i=0;i<arr.length;i++) {
            if (i>0) out += "~";
            out += safe(arr[i]);
        }
        return out;
    }

    private String joinFloats(float[] arr) {
        String out = "";
        for (int i=0;i<arr.length;i++) {
            if (i>0) out += ",";
            out += arr[i];
        }
        return out;
    }

    private float[] parseFloats(String s) {
        float[] out = new float[8];
        String[] p = s.split(",");
        for (int i=0;i<8;i++) {
            try { out[i] = Float.parseFloat(p[i]); }
            catch(Exception e) { out[i] = i == 0 ? .50f : .25f + (i % 3) * .25f; }
        }
        return out;
    }

    private String clean(String s, String fallback) {
        if (s == null) return fallback;
        s = s.trim();
        return s.isEmpty() ? fallback : s;
    }

    private int parse(String s) {
        try { return Integer.parseInt(s.trim()); } catch(Exception e) { return 0; }
    }

    private void hideKeyboard() {
        try {
            InputMethodManager imm = (InputMethodManager) getSystemService(INPUT_METHOD_SERVICE);
            View view = getCurrentFocus();
            if (imm != null && view != null) imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
        } catch (Exception ignored) {}
    }

    private void addGap(LinearLayout l, int h) {
        Space s = new Space(this);
        l.addView(s, new LinearLayout.LayoutParams(-1, dp(h)));
    }

    private int dp(int v) {
        return (int) (v * getResources().getDisplayMetrics().density + 0.5f);
    }

    static class Match {
        String date;
        int ga, gb;
        Team a, b;
        boolean hideAForUsers;
        boolean hideBForUsers;
    }

    static class Team {
        String name;
        String[] players;
        String substitute;
        String formation;
        float[] x = new float[8];
        float[] y = new float[8];

        Team(String name, String[] players, String substitute, String formation) {
            this.name = name;
            this.players = players;
            this.substitute = substitute;
            this.formation = formation;
            float[][] arr = TacticsView.getFormation(formation);
            for (int i=0;i<8;i++) {
                x[i] = arr[i][0];
                y[i] = arr[i][1];
            }
        }
    }

    public static class ManualTacticsView extends TacticsView {
        private int active = -1;

        public ManualTacticsView(Context c) {
            super(c);
        }

        @Override
        public boolean onTouchEvent(android.view.MotionEvent e) {
            if (team == null || pitchRect == null) return true;

            if (e.getAction() == android.view.MotionEvent.ACTION_DOWN) {
                active = nearest(e.getX(), e.getY());
                return true;
            }
            if (e.getAction() == android.view.MotionEvent.ACTION_MOVE && active >= 0) {
                float nx = (e.getX() - pitchRect.left) / pitchRect.width();
                float ny = (e.getY() - pitchRect.top) / pitchRect.height();
                if (nx < .06f) nx = .06f;
                if (nx > .94f) nx = .94f;
                if (ny < .06f) ny = .06f;
                if (ny > .94f) ny = .94f;
                team.x[active] = nx;
                team.y[active] = ny;
                invalidate();
                return true;
            }
            if (e.getAction() == android.view.MotionEvent.ACTION_UP || e.getAction() == android.view.MotionEvent.ACTION_CANCEL) {
                active = -1;
                return true;
            }
            return true;
        }

        private int nearest(float mx, float my) {
            int best = 0;
            double dist = 999999;
            for (int i=0;i<8;i++) {
                float px = pitchRect.left + pitchRect.width() * team.x[i];
                float py = pitchRect.top + pitchRect.height() * team.y[i];
                double d = Math.hypot(mx-px, my-py);
                if (d < dist) { dist = d; best = i; }
            }
            return best;
        }
    }

    public static class TacticsView extends View {
        protected Team team;
        protected Paint p = new Paint(Paint.ANTI_ALIAS_FLAG);
        protected RectF pitchRect;

        public TacticsView(Context c) {
            super(c);
            setLayerType(View.LAYER_TYPE_SOFTWARE, null);
        }

        public void setTeam(Team t) {
            this.team = t;
        }

        @Override
        protected void onDraw(Canvas c) {
            if (team == null) return;
            drawPoster(c, getWidth(), getHeight(), team);
        }

        protected void drawPoster(Canvas c, int w, int h, Team team) {
            p.setStyle(Paint.Style.FILL);
            p.setColor(Color.rgb(6,16,12));
            c.drawRect(0,0,w,h,p);

            RectF pitch = new RectF(w*.07f, h*.08f, w*.93f, h*.94f);
            pitchRect = pitch;

            drawStadium(c, pitch);
            drawPitch(c, pitch);

            p.setTextAlign(Paint.Align.CENTER);
            p.setTypeface(Typeface.DEFAULT_BOLD);
            p.setTextSize(h*.036f);
            p.setColor(Color.WHITE);
            c.drawText(team.name, w/2f, h*.055f, p);

            float[][] ps = positions(pitch, team);

            int[] nums = {1,2,3,4,5,6,7,8};
            for (int i=0;i<8;i++) drawPlayer(c, ps[i][0], ps[i][1], nums[i], team.players[i]);

            RectF sub = new RectF(w*.18f, h*.87f, w*.82f, h*.925f);
            p.setColor(Color.rgb(7,24,30));
            p.setStyle(Paint.Style.FILL);
            c.drawRoundRect(sub, 12, 12, p);
            p.setStyle(Paint.Style.STROKE);
            p.setColor(Color.rgb(216,180,90));
            p.setStrokeWidth(2);
            c.drawRoundRect(sub, 12, 12, p);
            p.setStyle(Paint.Style.FILL);
            p.setColor(Color.WHITE);
            p.setTextSize(h*.023f);
            c.drawText("البديل: " + team.substitute, sub.centerX(), sub.centerY()+8, p);
        }

        private void drawStadium(Canvas c, RectF pitch) {
            RectF outer = new RectF(pitch.left-36, pitch.top-36, pitch.right+36, pitch.bottom+24);
            p.setStyle(Paint.Style.FILL);
            p.setColor(Color.rgb(226,221,210));
            c.drawRoundRect(outer, 12, 12, p);

            p.setColor(Color.rgb(120,26,24));
            c.drawRoundRect(new RectF(pitch.left-10,pitch.top-10,pitch.right+10,pitch.bottom+10),6,6,p);

            RectF stand = new RectF(pitch.left, outer.top+8, pitch.right, pitch.top-14);
            p.setColor(Color.rgb(42,26,36));
            c.drawRoundRect(stand,6,6,p);
            p.setColor(Color.rgb(150,92,112));
            for(int i=1;i<5;i++) c.drawRect(stand.left, stand.top+i*stand.height()/5f, stand.right, stand.top+i*stand.height()/5f+3, p);

            p.setColor(Color.argb(45,80,80,80));
            p.setStrokeWidth(1);
            for(float x=outer.left;x<outer.right;x+=22) c.drawLine(x,outer.top,x,outer.bottom,p);
            for(float y=outer.top;y<outer.bottom;y+=22) c.drawLine(outer.left,y,outer.right,y,p);
            p.setStyle(Paint.Style.FILL);
        }

        private void drawPitch(Canvas c, RectF r) {
            p.setStyle(Paint.Style.FILL);
            int cols=12, rows=12;
            for(int i=0;i<cols;i++) {
                p.setColor(i%2==0?Color.rgb(25,140,30):Color.rgb(18,112,26));
                c.drawRect(r.left+i*r.width()/cols, r.top, r.left+(i+1)*r.width()/cols, r.bottom,p);
            }
            for(int i=0;i<cols;i++){
                for(int j=0;j<rows;j++){
                    p.setColor((i+j)%2==0?Color.argb(22,75,185,55):Color.argb(22,0,55,0));
                    c.drawRect(r.left+i*r.width()/cols, r.top+j*r.height()/rows,
                            r.left+(i+1)*r.width()/cols, r.top+(j+1)*r.height()/rows,p);
                }
            }

            p.setStyle(Paint.Style.STROKE);
            p.setStrokeWidth(Math.max(3, r.width()*.004f));
            p.setColor(Color.WHITE);
            c.drawRect(r,p);
            c.drawLine(r.left,r.centerY(),r.right,r.centerY(),p);
            c.drawCircle(r.centerX(),r.centerY(),r.width()*.12f,p);
            float boxW=r.width()*.36f, boxH=r.height()*.12f;
            c.drawRect(r.centerX()-boxW/2,r.top,r.centerX()+boxW/2,r.top+boxH,p);
            c.drawRect(r.centerX()-boxW/2,r.bottom-boxH,r.centerX()+boxW/2,r.bottom,p);
            float smallW=r.width()*.18f, smallH=r.height()*.055f;
            c.drawRect(r.centerX()-smallW/2,r.top,r.centerX()+smallW/2,r.top+smallH,p);
            c.drawRect(r.centerX()-smallW/2,r.bottom-smallH,r.centerX()+smallW/2,r.bottom,p);
            c.drawRect(r.centerX()-r.width()*.055f,r.top-11,r.centerX()+r.width()*.055f,r.top,p);
            c.drawRect(r.centerX()-r.width()*.055f,r.bottom,r.centerX()+r.width()*.055f,r.bottom+11,p);
            p.setStyle(Paint.Style.FILL);
        }

        protected float[][] positions(RectF r, Team team) {
            float[][] out = new float[8][2];
            if ("خطة يدوية".equals(team.formation)) {
                for (int i=0;i<8;i++) {
                    out[i][0] = r.left + r.width()*team.x[i];
                    out[i][1] = r.top + r.height()*team.y[i];
                }
                return out;
            }
            float[][] f = getFormation(team.formation);
            for (int i=0;i<8;i++) {
                out[i][0] = r.left + r.width()*f[i][0];
                out[i][1] = r.top + r.height()*f[i][1];
            }
            return out;
        }

        public static float[][] getFormation(String formation) {
            if ("3-2-2".equals(formation)) {
                return new float[][]{{.50f,.91f},{.22f,.74f},{.50f,.74f},{.78f,.74f},{.35f,.54f},{.65f,.54f},{.38f,.30f},{.62f,.30f}};
            } else if ("2-3-2".equals(formation)) {
                return new float[][]{{.50f,.91f},{.32f,.74f},{.68f,.74f},{.26f,.55f},{.50f,.55f},{.74f,.55f},{.38f,.30f},{.62f,.30f}};
            } else if ("1-3-3".equals(formation)) {
                return new float[][]{{.50f,.91f},{.50f,.74f},{.25f,.54f},{.50f,.54f},{.75f,.54f},{.27f,.30f},{.50f,.30f},{.73f,.30f}};
            }
            return new float[][]{{.50f,.91f},{.22f,.74f},{.50f,.74f},{.78f,.74f},{.30f,.50f},{.50f,.58f},{.70f,.50f},{.50f,.28f}};
        }

        private void drawPlayer(Canvas c,float x,float y,int num,String name){
            float rad=23;
            p.setShader(new RadialGradient(x-rad/3,y-rad/3,rad*1.5f,Color.rgb(10,90,210),Color.rgb(0,28,105), Shader.TileMode.CLAMP));
            p.setStyle(Paint.Style.FILL);
            c.drawCircle(x,y,rad,p);
            p.setShader(null);
            p.setStyle(Paint.Style.STROKE);
            p.setStrokeWidth(3);
            p.setColor(Color.WHITE);
            c.drawCircle(x,y,rad,p);
            p.setStyle(Paint.Style.FILL);
            p.setTextAlign(Paint.Align.CENTER);
            p.setTypeface(Typeface.DEFAULT_BOLD);
            p.setColor(Color.WHITE);
            p.setTextSize(21);
            c.drawText(String.valueOf(num),x,y+8,p);

            RectF card=new RectF(x-58,y+28,x+58,y+59);
            p.setColor(Color.rgb(7,24,30));
            c.drawRoundRect(card,8,8,p);
            p.setStyle(Paint.Style.STROKE);
            p.setStrokeWidth(2);
            p.setColor(Color.rgb(216,180,90));
            c.drawRoundRect(card,8,8,p);
            p.setStyle(Paint.Style.FILL);
            p.setColor(Color.WHITE);
            p.setTextSize(16);
            c.drawText(name,x,y+51,p);
        }
    }
}
