package com.example.ahmedmagdy.theclinic.Adapters;

import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseExpandableListAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.ahmedmagdy.theclinic.R;
import com.example.ahmedmagdy.theclinic.activities.NoteActivity;
import com.example.ahmedmagdy.theclinic.classes.BookingTimesClass;
import com.example.ahmedmagdy.theclinic.classes.NoteClass;
import com.example.ahmedmagdy.theclinic.classes.UtilClass;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;

public class BookingExpandableListAdapter extends BaseExpandableListAdapter {
    int c;
    private Context mContext;
    private FirebaseAuth mAuth;
    private List<BookingTimesClass> mListDataHeader; // header titles
    // child data in format of header title, child title
    private HashMap<BookingTimesClass, List<BookingTimesClass>> mListDataChild;

    public BookingExpandableListAdapter(Context _context, List<BookingTimesClass> _listDataHeader, HashMap<BookingTimesClass, List<BookingTimesClass>> _listDataChild) {
        this.mContext = _context;
        this.mListDataHeader = _listDataHeader;
        this.mListDataChild = _listDataChild;
        this.mAuth = FirebaseAuth.getInstance();
    }

    @Override
    public int getGroupCount() {
        return mListDataHeader.size();
    }

    @Override
    public int getChildrenCount(int i) {
        return mListDataChild.get(mListDataHeader.get(i)).size();
    }

    @Override
    public Object getGroup(int i) {
        return mListDataHeader.get(i);
    }

    @Override
    public Object getChild(int i, int i1) {  // i : group item position , i1 : child item position
        return mListDataChild.get(mListDataHeader.get(i))
                .get(i1);
    }

    @Override
    public long getGroupId(int i) {
        return i;
    }

    @Override
    public long getChildId(int i, int i1) {
        return i1;
    }

    @Override
    public boolean hasStableIds() {
        return false;
    }

    @Override
    public View getGroupView(int i, boolean b, View convertView, ViewGroup viewGroup) {
        BookingTimesClass header = (BookingTimesClass) getGroup(i);

        if (convertView == null) {
            LayoutInflater inflater = (LayoutInflater) mContext
                    .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            convertView = inflater.inflate(R.layout.list_header, viewGroup, false);
        }

        TextView bookingAddress = convertView.findViewById(R.id.booking_ad_tv);
        TextView bookingTime =  convertView.findViewById(R.id.booking_time_tv);

        bookingTime.setText(header.getCtStartTime() + " - " + header.getCtEndTime());
        bookingAddress.setText(header.getCtAddress());

        return convertView;
    }

    @Override
    public View getChildView(final int groupPosition, final int childPosition, boolean b, View convertView, ViewGroup viewGroup) {

        final BookingTimesClass currentChild = (BookingTimesClass) getChild(groupPosition, childPosition);

        if (convertView == null) {
            LayoutInflater infalInflater = (LayoutInflater) mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            convertView = infalInflater.inflate(R.layout.booking_list_item, viewGroup , false);
        }

        TextView patientName = (TextView) convertView.findViewById(R.id.patient_name_tv);
        TextView patientAge = (TextView) convertView.findViewById(R.id.patient_age_tv);
        TextView bookingHour = convertView.findViewById(R.id.patient_book_hour);
        TextView bookingTime = convertView.findViewById(R.id.book_time_tv);

        ImageView patientPicture = (ImageView) convertView.findViewById(R.id.patient_image);
        CheckBox patientdpcheckBox = (CheckBox) convertView.findViewById(R.id.patient_check_box) ;


        patientdpcheckBox.setChecked(currentChild.getChecked());
        patientdpcheckBox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, final boolean isChecked) {
                final BookingTimesClass currentChild = (BookingTimesClass) getChild(groupPosition, childPosition);
                /*************get new time *****************/
                Calendar calendar = Calendar.getInstance();
                SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
                String mDate = sdf.format(calendar.getTime());
                /*************get new time *****************/

                DatabaseReference databasePatient = FirebaseDatabase.getInstance().getReference("Doctorpatientdb")
                            .child(mAuth.getCurrentUser().getUid());


                final DatabaseReference mBookingRef = FirebaseDatabase.getInstance().getReference("bookingtimes")
                        .child(mAuth.getCurrentUser().getUid()).child(currentChild.getCttimeid())
                        .child(currentChild.getCtbookingdate());
              //  mBookingRef.child("JXxuQBgn2NUnSA8lypvipMPI6G23").child("ctArrangement").setValue(String.valueOf(1));
              //  mBookingRef.child("rLXUUcDyhdcaoHZEN0h55MIm0Gd2").child("ctArrangement").setValue(String.valueOf(2));

               /** databasetimeBooking.child(DoctorID) databasetimeBooking.child(DoctorID).child(timeID)
                .child(datedmy)
                .child(timesid).setValue(bookingtimesclass);**/
                final DatabaseReference bookforuser = FirebaseDatabase.getInstance().getReference("bookforuser");

                if (isChecked) {
                    mBookingRef.child(currentChild.getCtid()).child("checked").setValue(isChecked);

                    databasePatient.child(currentChild.getCtid()).setValue(currentChild);
                    databasePatient.child(currentChild.getCtid()).child("checked").setValue(isChecked);
                    databasePatient.child(currentChild.getCtid()).child("ctdate").setValue(mDate);

                    Toast.makeText(mContext ,  "This patient added to your database", Toast.LENGTH_LONG).show();

                    mBookingRef.addListenerForSingleValueEvent(new ValueEventListener() {
                                @Override
                                public void onDataChange(DataSnapshot dataSnapshot) {
                                     c=-1;
                                     for (DataSnapshot snap: dataSnapshot.getChildren()) {
                                         c = c + 1;
                                       if(c<childPosition){}else{

                                         String abcd = snap.child("ctArrangement").getValue(String.class);
                                         String ctid = snap.child("ctid").getValue(String.class);
                                         String arrandeid = snap.child("rangementid").getValue(String.class);

                                         int abcd1 = Integer.parseInt(abcd) - 1;
                                         // if(abcd1==childPosition){return;}
                                         if (abcd1 >= 0) {
                                             // if (abcd1 >= childPosition) {
                                             mBookingRef.child(ctid).child("ctArrangement").setValue(String.valueOf(abcd1));
                                             bookforuser.child(ctid).child(arrandeid).child("ctArrangement").setValue(String.valueOf(abcd1));
                                             // }
                                         }
                                     }
                                     }//end of for
                                    c=0;
                                }

                                @Override
                                public void onCancelled(DatabaseError databaseError) {

                                }
                            });

                    } else {
                    mBookingRef.child(currentChild.getCtid()).child("checked").setValue(isChecked);

                    databasePatient.child(currentChild.getCtid()).setValue(currentChild);
                    databasePatient.child(currentChild.getCtid()).child("checked").setValue(isChecked);
                    databasePatient.child(currentChild.getCtid()).child("ctdate").setValue(currentChild.getCtdate());

                    // databasePatient.child(currentChild.getCtid()).child("checked").setValue(isChecked);

                      //  Toast.makeText(context,  "Removed", Toast.LENGTH_LONG).show();

                    }

            }
        });

        String patAge = currentChild.getCtage();
        if (patAge != null){
            if (patAge.contains("_")){
                patAge = UtilClass.calculateAgeFromDate(patAge);

            }
            patientAge.setText(patAge);
        }

        patientName.setText(currentChild.getCtname());
        bookingHour.setText(currentChild.getCtPeriod());
        bookingTime.setText(currentChild.getCtdate());


        // notes dialog show select dialog on click list item
        convertView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showDialog("select",currentChild,childPosition);
            }
        });

        return convertView;
    }

    @Override
    public boolean isChildSelectable(int i, int i1) {
        return true;
    }

    /**
     *  Show or Add item data
     * @param item
     * @param position index of item in list
     */
    private void showDialog(String type, final BookingTimesClass item, final int position) {
        final Dialog dialog = new Dialog(mContext);
        final String userId = mAuth.getCurrentUser().getUid();
        Button create,show,cancel,submit;

        final DatabaseReference database = FirebaseDatabase.getInstance().getReference("Notes");

        // set dialog view
        if (type.equals("select")) {
            dialog.setContentView(R.layout.select_note_dialog);
            // set button
            create = dialog.findViewById(R.id.add_btn_select_note_dialog);
            show = dialog.findViewById(R.id.show_btn_select_note_dialog);
            cancel = dialog.findViewById(R.id.cancel_btn_select_note_dialog);
            // show all notes
            show.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(mContext,NoteActivity.class);
                    intent.putExtra("id",item.getCtid());
                    intent.putExtra("name",item.getCtname());
                    mContext.startActivity(intent);
                }
            });
            // add new note view
            create.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    showDialog("create",item,position);
                    dialog.dismiss();
                }
            });
            // cancel dialog
            cancel.setOnClickListener(new View.OnClickListener() {
                @Override
                 public void onClick(View v) {
                    dialog.dismiss();
                }
            });
        } else if (type.equals("create")) {
            dialog.setContentView(R.layout.add_note_dialog);
            //set btn
            submit = dialog.findViewById(R.id.submit_add_note);
            cancel = dialog.findViewById(R.id.cancel_add_note);
            // on submit create new note
            submit.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    EditText editText = dialog.findViewById(R.id.text_data_add_note);
                    String text = editText.getText().toString();
                    String date = UtilClass.getInstanceDate();
                    // check if edit text not empty
                    if (text.isEmpty()) {
                        editText.setError("Text is required");
                        editText.requestFocus();
                        return;
                    }
                    // add new note to database
                    DatabaseReference reference = database.push();
                    String noteId = reference.getKey();
                    NoteClass note = new NoteClass(noteId,text,item.getCtid(),userId,date);

                    database.child(noteId).setValue(note);
                    Toast.makeText(mContext,"Note Added.",Toast.LENGTH_SHORT).show();
                    // feed back message
                    /*
                    if(database.child(noteId).setValue(note).isSuccessful())
                        // success
                        Toast.makeText(mContext,"Note Added.",Toast.LENGTH_SHORT).show();
                    else
                        // fail
                        Toast.makeText(mContext,"Note Adding Fail, try again.",Toast.LENGTH_SHORT).show();
                    */
                    // clear dialog
                    dialog.dismiss();
                }
            });
            // cancel dialog
            cancel.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    dialog.dismiss();
                }
            });
        }

        // disable dialog close if tab out of it
        dialog.setCanceledOnTouchOutside(false);
        // show dialog view
        dialog.show();
    }
}