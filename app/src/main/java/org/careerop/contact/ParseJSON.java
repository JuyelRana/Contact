package org.careerop.contact;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Juyel on 17/05/09.
 */

public class ParseJSON {
    private String json;
    public static List<Contact> contactList;
    private JSONArray jsonArray = null;

    public ParseJSON(String json) {
        this.json = json;
    }

    protected void jsonParser() {
        contactList = new ArrayList<>();
        JSONObject jsonObject = null;
        try {
            jsonObject = new JSONObject(json);
            jsonArray = jsonObject.getJSONArray(Config.JSON_ARRAY);
            for (int i = 0; i < jsonArray.length(); i++) {
                JSONObject object = jsonArray.getJSONObject(i);
                Contact contact = new Contact();
                contact.setId(object.getString(Config.ID));
                contact.setName(object.getString(Config.NAME));
                contact.setPhone(object.getString(Config.PHONE));
                contact.setImage(object.getString(Config.IMAGE));
                contactList.add(contact);
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }
}
