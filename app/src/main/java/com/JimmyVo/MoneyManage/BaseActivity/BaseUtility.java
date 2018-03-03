package com.JimmyVo.MoneyManage.BaseActivity;

import android.app.Activity;
import android.content.DialogInterface;
import android.graphics.Color;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.Gravity;
import android.view.MotionEvent;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TableRow;
import android.widget.TextView;

import com.JimmyVo.MoneyManage.R;
import com.JimmyVo.MoneyManage.Utility.Message;

import java.util.ArrayList;

import static com.JimmyVo.MoneyManage.Utility.CommonU.amountStringVerify;

/**
 * Created by Duy Vo on 12/31/2017.
 */

public abstract class BaseUtility extends BaseActivity {
    static int PARA_TEXT_PADDING_H = 10;
    static int PARA_TEXT_PADDING_V = 10;
    protected static int PARA_TEXT_SIZE = 20;


    protected class ContenViewFormat{
        public ContenViewFormat(int normalColor,int selectedColor,  int typeFace, int textSize) {
            this.selectedColor = selectedColor;
            this.normalColor = normalColor;
            this.typeFace = typeFace;
            this.textSize = textSize;
        }

        public int selectedColor ;
        public int normalColor;
        public int typeFace;
        public int textSize;
    }


    public abstract class ConfirmDialog {
        public ConfirmDialog(String title, String mess) {
            AlertDialog alertDialog = new AlertDialog.Builder(BaseUtility.this).create();
            alertDialog.setTitle(title);
            alertDialog.setMessage(mess);
            alertDialog.setButton(AlertDialog.BUTTON_NEGATIVE, "Cancel", new DialogInterface.OnClickListener()

            {
                public void onClick(DialogInterface alertDialog, int which) {
                    alertDialog.dismiss();
                }
            });
            alertDialog.setButton(AlertDialog.BUTTON_POSITIVE, "OK", new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface alertDialog, int which) {
                    onAccept();
                    alertDialog.dismiss();
                }
            });
            alertDialog.show();
        }
        protected abstract void onAccept();
    }


    public abstract class ConfirmTextDialog {
        EditText text;
        public ConfirmTextDialog(String title, String mess, final String content) {

            String oldText = content;
            text = createEditText(content);
            text.setTextAlignment(View.TEXT_ALIGNMENT_CENTER);
            new SetTextWatcher(text) {
                @Override
                protected void onTextChange(String string) {
                    onTextChanged(text);
                }
            };
            LinearLayout linearLayout = new LinearLayout(BaseUtility.this);
            linearLayout.setOrientation(LinearLayout.HORIZONTAL);
            linearLayout.addView(text);
            linearLayout.setPadding(15,15,15,15);
            AlertDialog alertDialog = new AlertDialog.Builder(BaseUtility.this).create();
            alertDialog.setTitle(title);
            alertDialog.setMessage(mess);
            alertDialog.setView(linearLayout);
            alertDialog.setButton(AlertDialog.BUTTON_NEGATIVE, "Cancel", new DialogInterface.OnClickListener()
            {
                public void onClick(DialogInterface alertDialog, int which) {
                    alertDialog.dismiss();
                }
            });
            alertDialog.setButton(AlertDialog.BUTTON_POSITIVE, "OK", new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface alertDialog, int which) {
                    if(onAccept( text.getText().toString()))
                    alertDialog.dismiss();
                }
            });
            alertDialog.show();
        }
        protected abstract boolean onAccept(String value);

        protected void onTextChanged(EditText string){
            if(string.getText().toString().contains("\n") || string.getText().toString().contains("\t")){
                text.setTextColor(getResources().getColor(R.color.colorTextError));
            } else {
                text.setTextColor(getResources().getColor(R.color.colorTextDefault));
            }
        }

    }



    public abstract class DropdownList extends Activity{
        private final Spinner spinner;
        private int lastSelected = -1;
        public DropdownList(ArrayList<String> spinnerArray, int selection){
            spinner = new Spinner(BaseUtility.this);
            lastSelected = selection;
            ArrayAdapter<String> spinnerArrayAdapter = new ArrayAdapter<String>(BaseUtility.this.getApplicationContext(),
                    android.R.layout.simple_spinner_dropdown_item, spinnerArray);
            spinner.setAdapter(spinnerArrayAdapter);
            spinner.setSelection(selection);
            spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener(){

                @Override
                public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                    if(lastSelected != spinner.getSelectedItemPosition()) {
                        onNewItemSelected(spinner.getSelectedItemPosition(), adapterView.getSelectedItem().toString());
                        lastSelected = i;
                    }
                    ((TextView) adapterView.getChildAt(0)).setTextColor(Color.WHITE);
                }

                @Override
                public void onNothingSelected(AdapterView<?> adapterView) {

                }
            });
        }

        public Spinner getSpinner(){return spinner;}

        protected abstract void onNewItemSelected(int selectedItemPosition, String string);
    }


    protected Spinner createSpinner(ArrayList<String> spinnerArray, int selection){
        Spinner spinner = new Spinner(this);
        spinner.setId(View.generateViewId());
        ArrayAdapter<String> spinnerArrayAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_dropdown_item, spinnerArray);
        spinner.setAdapter(spinnerArrayAdapter);
        spinner.setSelection(selection);
        return spinner;
    }

    protected EditText createEditText(String text){
        EditText textViews = new EditText(this);
        textViews.setTextSize(25);
        textViews.setTextAlignment(View.TEXT_ALIGNMENT_TEXT_START);


        TableRow.LayoutParams params = new TableRow.LayoutParams(
                TableRow.LayoutParams.WRAP_CONTENT,
                TableRow.LayoutParams.MATCH_PARENT,
                Gravity.CENTER_VERTICAL | Gravity.CENTER_HORIZONTAL);
        params.weight = 10;
        textViews.setPadding(10,10,10, 10);
        params.setMargins(30,10,10,10);
        textViews.setLayoutParams(params);

        textViews.setText(text);
        textViews.setBackgroundColor(Color.TRANSPARENT);
        return textViews;
    }

    protected TextView createTextView(String text){
        TextView textViews = new TextView(this);
        textViews.setTextSize(25);
        textViews.setTextAlignment(View.TEXT_ALIGNMENT_TEXT_START);


        TableRow.LayoutParams params = new TableRow.LayoutParams(
                TableRow.LayoutParams.WRAP_CONTENT,
                TableRow.LayoutParams.MATCH_PARENT,
                Gravity.CENTER_VERTICAL | Gravity.CENTER_HORIZONTAL);
        params.weight = 10;
        textViews.setPadding(PARA_TEXT_PADDING_H,PARA_TEXT_PADDING_V,PARA_TEXT_PADDING_H,PARA_TEXT_PADDING_V);;
        params.setMargins(30,20,20,10);
        textViews.setLayoutParams(params);

        textViews.setText(text);
        textViews.setBackgroundColor(Color.TRANSPARENT);
        return textViews;
    }

    protected TextView createTextView(ContenViewFormat format, String text , int alignment){
        TextView textView = new TextView(this);
        textView.setTextSize(format.textSize);
        textView.setId(TextView.generateViewId());
        textView.setPadding(PARA_TEXT_PADDING_H,PARA_TEXT_PADDING_V,PARA_TEXT_PADDING_H,PARA_TEXT_PADDING_V);
        textView.setBackgroundColor(format.normalColor);
        textView.setTypeface(textView.getTypeface(),format.typeFace);
        textView.setText(text);
        textView.setTextAlignment(alignment);
        return textView;
    }

    protected ImageButton createButtonView(int idResource) {
        ImageButton button = new ImageButton(this);
        button.setImageResource(idResource);
        button.setBackgroundColor(Color.TRANSPARENT);
        button.setId(View.generateViewId());
        button.setPadding(5,5,5, 5);
        TableRow.LayoutParams params =
                new TableRow.LayoutParams(
                        TableRow.LayoutParams.WRAP_CONTENT,
                        TableRow.LayoutParams.MATCH_PARENT,
                        Gravity.CENTER_VERTICAL | Gravity.CENTER_HORIZONTAL);
        params.setMargins(5,5,5,5);
        params.weight = 1;
        button.setLayoutParams(params);

        button.setOnTouchListener(new ImageButton.OnTouchListener() {

            public boolean onTouch(View v, MotionEvent event) {
                switch (event.getAction()) {
                    case MotionEvent.ACTION_DOWN:
                    case MotionEvent.ACTION_MOVE:
                        v.setBackgroundColor((getResources().getColor(R.color.selectedRow)));
                        v.invalidate();
                        break;
                    default:
                        v.setBackgroundColor(Color.TRANSPARENT);
                        v.invalidate();
                        break;
                }
                return false;
            }
        });
        return button;
    }
    public void createEditTextDoubleFilter (final EditText textView){
        textView.addTextChangedListener(new TextWatcher() {

            public void afterTextChanged(Editable s) {
                if (amountStringVerify(textView.getText().toString())){
                    textView.setTextColor(getResources().getColor(R.color.colorTextDefault));
                } else {
                    textView.setTextColor(getResources().getColor(R.color.colorTextError));
                }
            }
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}
            public void onTextChanged(CharSequence s, int start, int before, int count) { }
        });
    }

    public abstract class SetTextWatcher {
        public SetTextWatcher(final EditText textView) {
            textView.addTextChangedListener(new TextWatcher() {

                public void afterTextChanged(Editable s) {
                    onTextChange(textView.getText().toString());
                }

                public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                }

                public void onTextChanged(CharSequence s, int start, int before, int count) {
                }
            });
        }

        protected abstract void onTextChange(String string);
    }
}
