#### How to create an application using GWTExpress ####
> TBD
#### How to change the Search Tab text ####
`myExpressPage.setSearchTabText(searchTabText);`
#### How to display the Create page by default instead of the Search page? ####
`myExpressPage.setShowSearchFirst(false);`
#### How to disable editing? ####
`myExpressPage.setEditAllowed(false);`
#### How to disable search? ####
`myExpressPage.setSearchAllowed(false);`
#### How to set auto query? ####
`myExpressPage.setAutoQueryParams(String[] autoQueryParams);`
#### How to perform custom field validation? ####
Refer [MyAccount.java](http://code.google.com/p/gwtexpress/source/browse/trunk/Client/src/com/gwtexpress/client/ui/ex/secure/MyAccount.java) & [FieldHandler.java](http://code.google.com/p/gwtexpress/source/browse/trunk/Client/src/com/gwtexpress/client/ui/form/FieldHandler.java)
```
        FieldHandler fh = new FieldHandler() {
                public String isValid(Widget field) {
                    PasswordTextBox pw1 = 
                        (PasswordTextBox)pwdChangeForm.getField("NEW_PASSWORD1");
                    PasswordTextBox pw2 = 
                        (PasswordTextBox)pwdChangeForm.getField("NEW_PASSWORD2");
                    if (field == pw1 || field == pw2) {
                        String p1 = pw1.getText();
                        String p2 = pw2.getText();
                        if (p1 != null && p2 != null && p1.length() > 0 && 
                            p2.length() > 0 && !p1.equals(p2)) {
                            return "New passwords doesn't match each other. Please verify.";
                        }
                    }
                    return null;
                }
            };
        pwdChangeForm.addFieldHandler("OLD_PASSWORD", fh);
        pwdChangeForm.addFieldHandler("NEW_PASSWORD1", fh);
        pwdChangeForm.addFieldHandler("NEW_PASSWORD2", fh);
```
#### How to ensure that the user selects a valid value from the SuggestBox suggestions? ####
```
        SuggestBox2 suggest = 
            (SuggestBox2)myForm.findField("<ColumnName>");
        FieldHandler fieldHandler = new FieldHandler() {
                public String isValid(Widget field) {
                    SuggestBox2 suggest = ((SuggestBox2)field);
                    if (suggest.getSuggestOracle().isValid(suggest.getText())) {
                        return null;
                    } else {
                        return "Invalid value. Please select a value from the suggested list.";
                    }
                }
            };
        myForm.addFieldHandler(suggest, fieldHandler);
```
#### How to populate ListBox or SuggestBox from a custom table instead of lookup table? ####
In you metadata definition specify the lookup name as "LOV:<CUSTOM VALUE>" and on the server write your customer query for <CUSTOM VALUE> as shown below.
Refer [Worker.java](http://code.google.com/p/gwtexpress/source/browse/trunk/Model/src/com/gwtexpress/server/Worker.java)
```
    public ArrayList<String[]> getLOVData(String lovName, 
                                          String[] params) throws SQLException {
        if ("USER_ID".equals(lovName)) {
            MetaDataObject metaDataObject;
            metaDataObject = 
                    dbMetaDataObject.getMetaDataObject("SELECT USER_ID, USER_NAME FROM user_accounts ORDER BY 2", 
                                                       null);
            return metaDataObject.getData();
        } else {
            return null;
        }
    }
```