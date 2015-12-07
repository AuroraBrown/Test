package net.aurorabrown.test;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.app.Activity;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import org.w3c.dom.Text;

import java.util.ArrayList;

public class MainActivity extends Activity implements OnClickListener {

    int taskIndex;
    private String storedTaskIndex = "storedTaskIndex";
    EditText actionInput, numInput, unitInput, descInput;

    TextView displayedScore;
    private String storedScore = "storedScore";

    MyCustomAdapter dataAdapter = null;
    private ArrayList<Task> taskList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        refreshHomeElements();
    }

    @Override
    public void onClick(View v) {
        if (v.getId() == R.id.addExpPointsButton) {
            addExpPoints(10);
        }
    }

    public void goToProfile(View v) {
        setContentView(R.layout.profile);
    }

    public void goHome(View v) {
        setContentView(R.layout.activity_main);
        refreshHomeElements();
    }

    public void refreshHomeElements() {
        setupVariables();
        restoreSettings();
        displayListView();
        checkButtonClick();
    }

    public void createNewTask(View v) {
        setContentView(R.layout.add_task);

    }

    public void createTask(View v) {
        String i = Integer.toString(taskIndex);
        actionInput = (EditText) findViewById(R.id.newTaskVerbInput);
        numInput = (EditText) findViewById(R.id.newTaskNumInput);
        unitInput = (EditText) findViewById(R.id.newTaskUnitInput);
        descInput = (EditText) findViewById(R.id.newTaskDescInput);

        String actionInputString = actionInput.getText().toString();
        String numInputString = numInput.getText().toString();
        String unitInputString = unitInput.getText().toString();
        String descInputString = descInput.getText().toString();

        String codeKey = descInputString;
        String nameKey = actionInputString + numInputString + unitInputString;

        // Commit new string values and update the taskIndex
        getSharedPreferences("storedCodeKey" + i, 0).edit().putString("storedCodeKey" + i, codeKey).apply();
        getSharedPreferences("storedNameKey" + i, 0).edit().putString("storedNameKey" + i, nameKey).apply();

        taskIndex = taskIndex + 1;
        getSharedPreferences(storedTaskIndex, 0).edit().putInt(storedTaskIndex, taskIndex).apply();

        //add new task to array and update list
        setContentView(R.layout.activity_main);
        refreshHomeElements();
    }

    private void setupVariables() {
        taskIndex = getSharedPreferences(storedTaskIndex,0).getInt(storedTaskIndex, 0);
        displayedScore = (TextView) findViewById(R.id.tvSavedValueDisplay);
        findViewById(R.id.addExpPointsButton).setOnClickListener(this);
        buildTaskArray();
    }

    protected void restoreSettings() {
        String whatScore = getSharedPreferences(storedScore,0).getString(storedScore, "0");
        displayedScore.setText(whatScore);
    }

    public void addExpPoints(int amount) {
        // Update exp to date
        String prevExp = getSharedPreferences(storedScore, 0).getString(storedScore, "0");
        String points = Integer.toString(Integer.parseInt(prevExp) + amount);
        getSharedPreferences(storedScore, 0).edit().putString(storedScore, points).apply();
        displayedScore.setText(points);
    }

    public void buildTaskArray() {
        //Array list of countries
        taskList = new ArrayList<Task>();
        for (int i = 0; i < taskIndex; i++) {
            String index = Integer.toString(i);
            String code = getSharedPreferences("storedCodeKey" + index,0).getString("storedCodeKey" + index, " ");
            String name = getSharedPreferences("storedNameKey" + index,0).getString("storedNameKey" + index, " ");
            Task task = new Task(code, name, false);
            taskList.add(task);
        }
    }

    private void displayListView() {
        dataAdapter = new MyCustomAdapter(this, R.layout.task_info, taskList);
        ListView listView = (ListView) findViewById(R.id.listView1);
        // Assign adapter to ListView
        listView.setAdapter(dataAdapter);

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                // When clicked, show a toast with the TextView text
                Task task = (Task) parent.getItemAtPosition(position);
                Toast.makeText(getApplicationContext(),
                        "Clicked on Row: " + task.getName(),
                        Toast.LENGTH_LONG).show();
            }
        });
    }

    private class MyCustomAdapter extends ArrayAdapter<Task> {

        private ArrayList<Task> taskList;
        public MyCustomAdapter(Context context, int textViewResourceId, ArrayList<Task> taskList) {
            super(context, textViewResourceId, taskList);
            this.taskList = new ArrayList<Task>();
            this.taskList.addAll(taskList);
        }

        private class ViewHolder {
            TextView code;
            CheckBox name;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {

            ViewHolder holder = null;
            Log.v("ConvertView", String.valueOf(position));
            if (convertView == null) {
                LayoutInflater vi = (LayoutInflater)getSystemService(Context.LAYOUT_INFLATER_SERVICE);
                convertView = vi.inflate(R.layout.task_info, null);

                holder = new ViewHolder();
                holder.code = (TextView) convertView.findViewById(R.id.code);
                holder.name = (CheckBox) convertView.findViewById(R.id.checkBox1);
                convertView.setTag(holder);

                holder.name.setOnClickListener( new View.OnClickListener() {
                    public void onClick(View v) {
                        CheckBox cb = (CheckBox) v ;
                        Task task = (Task) cb.getTag();
                        Toast.makeText(getApplicationContext(), "Clicked on Checkbox: " + cb.getText() + " is " + cb.isChecked(), Toast.LENGTH_LONG).show();
                        task.setSelected(cb.isChecked());
                    }
                });
            }
            else {
                holder = (ViewHolder) convertView.getTag();
            }

            Task task = taskList.get(position);
            holder.code.setText(" (" +  task.getCode() + ")");
            holder.name.setText(task.getName());
            holder.name.setChecked(task.isSelected());
            holder.name.setTag(task);
            return convertView;
        }
    }

    public void updateListNumbers(int start) {

        for (int i = start; i < taskIndex - 1; i++) {
            if (taskList.get(i) != null) {
                String lower = Integer.toString(i);
                String upper = Integer.toString(i + 1);
                String code = getSharedPreferences("storedCodeKey" + upper, 0).getString("storedCodeKey" + upper, " ");
                String name = getSharedPreferences("storedNameKey" + upper, 0).getString("storedNameKey" + upper, " ");

                // Commit new string values and update the taskIndex
                getSharedPreferences("storedCodeKey" + lower, 0).edit().putString("storedCodeKey" + lower, code).apply();
                getSharedPreferences("storedNameKey" + lower, 0).edit().putString("storedNameKey" + lower, name).apply();
            }
        }
    }

    private void checkButtonClick() {
        Button myButton = (Button) findViewById(R.id.findSelected);
        myButton.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {

                StringBuffer responseText = new StringBuffer();
                responseText.append("Task(s) complete!\n");

                for (int i = 0; i < taskList.size(); i++) {
                    Task task = taskList.get(i);
                    if (task.isSelected()) {
                        responseText.append("\n" + task.getName());
                        taskList.remove(i);

                        updateListNumbers(i);
                        taskIndex = taskIndex - 1;
                        getSharedPreferences(storedTaskIndex, 0).edit().putInt(storedTaskIndex, taskIndex).apply();
                        refreshHomeElements();
                    }
                }
            }
        });

    }

}
