package com.devtwist.easyrecord.Adapters;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.devtwist.easyrecord.Models.CustomerData;
import com.devtwist.easyrecord.Models.RecordData;
import com.devtwist.easyrecord.R;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.List;

public class SearchAdapter extends RecyclerView.Adapter<SearchAdapter.SearchViewHolder> {

    private String senderCnic, headingText;
    private Context context;
    private List<RecordData> recordList;

    public SearchAdapter(Context context, List<RecordData> recordList, String headingText) {
        this.context = context;
        this.recordList = recordList;
        this.headingText = headingText;
    }

    @NonNull
    @Override
    public SearchViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.search_items, parent, false);
        return new SearchViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull SearchViewHolder holder, int position) {
        try {
            Log.i("SearchAdapter","successfull");
            RecordData model = recordList.get(position);
            if (model.gettType().equals("Deposit")){
                holder._headingItem.setText(model.gettType());
                holder._headingItem.setBackgroundResource(R.drawable.deposit_heading_bg);
            }else{
                holder._headingItem.setText(model.gettType());
                holder._headingItem.setBackgroundResource(R.drawable.widraw_heading_bg);
            }
            senderCnic = model.getCustomerCnicNo();
            FirebaseDatabase.getInstance().getReference().child("Customers").child(senderCnic)
                            .addListenerForSingleValueEvent(new ValueEventListener() {
                                @Override
                                public void onDataChange(@NonNull DataSnapshot snapshot) {
                                    CustomerData customerData = snapshot.getValue(CustomerData.class);
                                    holder._senderNameItem.setText(customerData.getCustomerName());
                                }

                                @Override
                                public void onCancelled(@NonNull DatabaseError error) {

                                }
                            });
            holder._tIdItem.setText(model.gettId());
            holder._tPhoneNoItem.setText(model.gettPhone());
            holder._tDateItem.setText(model.gettDate());
            holder._tAmountItem.setText(model.gettAmount());
            //holder._tTypeItem.setText(model.gettType());
            holder._companyItem.setText(model.getCompany());
            holder._senderCnicItem.setText(senderCnic);

        } catch (Exception e) {
            e.printStackTrace();

        }
    }

    @Override
    public int getItemCount() {
        return recordList.size();
    }

    class SearchViewHolder extends RecyclerView.ViewHolder{
        private TextView _headingItem, _senderNameItem, _senderCnicItem, _tIdItem, _tPhoneNoItem, _tDateItem, _tAmountItem, _tTypeItem, _companyItem;
        public SearchViewHolder(@NonNull View itemView) {
            super(itemView);

            try {
                _headingItem = (TextView) itemView.findViewById(R.id._headingItem);
                _senderNameItem = (TextView) itemView.findViewById(R.id._senderNameItem);
                _senderCnicItem = (TextView) itemView.findViewById(R.id._senderCnicItem);
                _tIdItem = (TextView) itemView.findViewById(R.id._tIdItem);
                _tPhoneNoItem = (TextView) itemView.findViewById(R.id._tPhoneNoItem);
                _tDateItem = (TextView) itemView.findViewById(R.id._tDateItem);
                _tAmountItem = (TextView) itemView.findViewById(R.id._tAmountItem);
                //_tTypeItem = (TextView) itemView.findViewById(R.id._tTypeItem);
                _companyItem = (TextView) itemView.findViewById(R.id._companyItem);
            } catch (Exception e) {
                Log.i("ViewHolder Error", e.getMessage().toString());
            }
        }
    }
}
