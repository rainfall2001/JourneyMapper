package com.example.mapper.helper;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.widget.EditText;

import androidx.appcompat.app.AlertDialog;

import com.example.mapper.activity.SearchAddressActivity;
import com.example.mapper.model.Address;

/**
 * This class only has static methods which build or show alert dialogs. Multiple classes use the address dialog to allow the
 * user to add a new address.
 * @author Rhane Mercado
 */
public class AlertDialogHelper {

    /**
     * This method will build and show a alert dialog. The user is able to input the address name and if
     * it is valid they will be able to search the physical address.
     */
    private static void getNewAddressDialog(Context context){
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        //create a space for the user to enter a new address name
        EditText addressName = new EditText(context);
        addressName.setHint("eg: Home");
        builder.setView(addressName);

        builder.setTitle("Select Address");
        builder.setPositiveButton("Add New Address", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                //boolean present = Address.checkAddressName(getContext(), addressName);
                boolean present = Address.checkAddressName(addressName.getText().toString());
                if(!present){
                    //create a new intent
                    Intent searchIntent = new Intent(context, SearchAddressActivity.class);
                    //pass the address name
                    searchIntent.putExtra("addressName", addressName.getText().toString());
                    context.startActivity(searchIntent);
                }
            }
        });
        AlertDialog dialog = builder.create();
        dialog.show();
    }

    /**
     * This method builds an AlertDialog.Builder with the variables passed in.
     * @param context The context of the class
     * @param title The title of the dialog
     * @param message The message of the dialog
     * @return AlertDialog.Builder
     */
    public static AlertDialog.Builder getBuilder(Context context, String title, String message){
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setTitle(title)
                .setMessage(message);
        return builder;
    }

    /**
     * This method builds an AlertDialog.Builder with a neutral button which has a preset method
     * @param context The context of the class
     * @return AlertDialog.Builder
     */
    public static AlertDialog.Builder getAddressDialog(Context context){
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setTitle("Select Start Address")
                .setNeutralButton("Add New Address", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        getNewAddressDialog(context);
                    }
                });
        return builder;
    }
}
