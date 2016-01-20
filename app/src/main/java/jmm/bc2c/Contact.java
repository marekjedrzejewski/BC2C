package jmm.bc2c;

import android.content.Intent;
import android.provider.ContactsContract;

public class Contact {

    // returns intent that has to be started
    public static Intent CreateContactIntent(String name, String phonenumber) {
        // Creates a new Intent to insert a contact
        Intent intent = new Intent(ContactsContract.Intents.Insert.ACTION);
        // Sets the MIME type to match the Contacts Provider
        intent.setType(ContactsContract.RawContacts.CONTENT_TYPE);

        intent.putExtra(ContactsContract.Intents.Insert.NAME, name)
              .putExtra(ContactsContract.Intents.Insert.PHONE, phonenumber);

        return intent;
    }

}
