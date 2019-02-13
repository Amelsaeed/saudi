package com.example.ahmedmagdy.theclinic.Adapters;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.TextView;
import android.widget.Toast;

import com.example.ahmedmagdy.theclinic.R;
import com.example.ahmedmagdy.theclinic.classes.NoteClass;
import com.example.ahmedmagdy.theclinic.classes.UtilClass;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.ArrayList;
import java.util.List;

public class NoteAdapter extends ArrayAdapter<NoteClass> implements Filterable {
    private List<NoteClass> mList;
    private Activity mContext;
    private List<NoteClass> mSearchList;
    private FirebaseAuth mAuth;

    public NoteAdapter(Activity context, List<NoteClass> mList) {
        super(context, R.layout.list_layout_doctors, mList);
        this.mContext = context;
        this.mList = mList;
    }

    @NonNull
    @Override
    public View getView(final int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        // if no view to reuse create new one
        if (convertView == null) {
            LayoutInflater infalInflater = (LayoutInflater) mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            convertView = infalInflater.inflate(R.layout.note_list, parent , false);
        }
        // Note current data
        final NoteClass note = mList.get(position);
        // set view
        final TextView displayTextView = convertView.findViewById(R.id.text_display_note);
        final EditText displayNewEditTextView = convertView.findViewById(R.id.new_text_display_note);
        final TextView displayDateView = convertView.findViewById(R.id.text_date_display_note);
        // btn
        final Button edit = convertView.findViewById(R.id.edit_btn_display_note);
        final Button delete = convertView.findViewById(R.id.delete_btn_display_note);
        final Button save = convertView.findViewById(R.id.save_btn_display_note);
        // set data to view
        mAuth = FirebaseAuth.getInstance();
        if(! mAuth.getCurrentUser().getUid().equals(note.getcDoctorId())){
            edit.setVisibility(View.GONE);
            delete.setVisibility(View.GONE);
        }else{
            edit.setVisibility(View.VISIBLE);
            delete.setVisibility(View.VISIBLE);
        }
        displayTextView.setText(note.getcText());
        displayDateView.setText(note.getcDate());
        // on click edit btn
        edit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // change views
                displayTextView.setVisibility(View.GONE);
                displayNewEditTextView.setVisibility(View.VISIBLE);
                edit.setVisibility(View.GONE);
                save.setVisibility(View.VISIBLE);
                // update view data
                displayNewEditTextView.setText(note.getcText());
            }
        });
        // on click delete btn
        delete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // show confirm dialog
                showDialog("delete_confirm",note,position,null);
            }
        });
        // on click save btn
        save.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // show confirm dialog
                showDialog("save_confirm",note,position,displayNewEditTextView);
                // update view data
                displayDateView.setText(note.getcText());
                // change views
                displayTextView.setVisibility(View.VISIBLE);
                displayNewEditTextView.setVisibility(View.GONE);
                edit.setVisibility(View.VISIBLE);
                save.setVisibility(View.GONE);
            }
        });
        Animation animation = AnimationUtils.loadAnimation(mContext,R.anim.scale);
        convertView.startAnimation(animation);
        return convertView;
    }

    @Override
    public int getCount() {
        return mList.size();
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @NonNull
    @Override
    public Filter getFilter() {
        return new Filter() {
            @Override
            protected FilterResults performFiltering(CharSequence constraint) {
                final FilterResults resultsFilter = new FilterResults();
                final List<NoteClass> resultsList = new ArrayList<>();
                if (mSearchList == null)
                    mSearchList = mList;
                if (constraint != null) {
                    if (mSearchList != null && mSearchList.size() > 0) {
                        for (final NoteClass note : mSearchList) {
                            if (note.getcText().toLowerCase()
                                    .contains(constraint.toString()))
                                resultsList.add(note);
                        }
                    }
                    resultsFilter.values = resultsList;
                }
                return resultsFilter;
            }

            @Override
            protected void publishResults(CharSequence constraint, FilterResults results) {
                mList = (ArrayList<NoteClass>) results.values;
                notifyDataSetChanged();
            }
        };
    }

    /**
     *  Show or Add item data
     * @param note
     * @param position index of item in list
     */
    private void showDialog(String type, final NoteClass note, final int position, final EditText editText) {
        final Dialog dialog = new Dialog(mContext);
        Button create,show,ok,cancel;

        final DatabaseReference database = FirebaseDatabase.getInstance().getReference("Notes").child(note.getcId());

        dialog.setContentView(R.layout.select_note_dialog);
        // set button
        ok = dialog.findViewById(R.id.ok_btn_select_note_dialog);
        cancel = dialog.findViewById(R.id.cancel_btn_select_note_dialog);
        create = dialog.findViewById(R.id.add_btn_select_note_dialog);
        show = dialog.findViewById(R.id.show_btn_select_note_dialog);
        // update view
        create.setVisibility(View.GONE);
        show.setVisibility(View.GONE);
        cancel.setVisibility(View.VISIBLE);
        ok.setVisibility(View.VISIBLE);
        // set dialog view
        if (type.equals("delete_confirm")) {
            ok.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    database.removeValue();
                    // success
                    Toast.makeText(mContext, R.string.note_deleted, Toast.LENGTH_SHORT).show();
                    // remove date form list
                    mList.remove(position);
                    notifyDataSetChanged();
                    // clear dialog
                    dialog.dismiss();
                    /*
                    if(database.removeValue().isSuccessful()) {
                        // success
                        Toast.makeText(mContext, "Note Deleted.", Toast.LENGTH_SHORT).show();
                        // remove date form list
                        mList.remove(position);
                    } else
                        // fail
                        Toast.makeText(mContext,"Delete Data Fail, Please try again",Toast.LENGTH_SHORT).show();
                     */
                }
            });
        } else if (type.equals("save_confirm")) {
            // on submit create new note
            ok.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    String text = editText.getText().toString();
                    String date = UtilClass.getInstanceDate();
                    // check if edit text not empty
                    if (text.isEmpty()) {
                        editText.setError(mContext.getString(R.string.text_is_required));
                        editText.requestFocus();
                        return;
                    }
                    // add new note to database
                    String id = note.getcId();
                    String noteUserId = note.getcUserId();
                    String doctorId = note.getcDoctorId();
                    NoteClass note = new NoteClass(id,text,noteUserId,doctorId,date);
                    // update database
                    database.setValue(note);
                    // update list
                    mList.get(position).setcText(text);
                    mList.get(position).setcDate(date);
                    notifyDataSetChanged();
                    // feed back message
                    Toast.makeText(mContext,R.string.note_updated,Toast.LENGTH_SHORT).show();
                    /*
                    if(database.setValue(note).isSuccessful())
                        // success
                        Toast.makeText(mContext,"Note Updated.",Toast.LENGTH_SHORT).show();
                    else
                        // fail
                        Toast.makeText(mContext,"Update Data Fail, Please try again",Toast.LENGTH_SHORT).show();
                     */
                    // clear dialog
                    dialog.dismiss();
                }
            });
        }

        // cancel dialog
        cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });

        // disable dialog close if tab out of it
        dialog.setCanceledOnTouchOutside(false);
        // show dialog view
        dialog.show();
    }
}
