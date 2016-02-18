# Introduction #

This project is aimed to develope a framework to enable express development of GWT Database oriented Application with very less coding

# Simple things first... #

Let's say we need to build a small application for the maintenance of the below table
```
CREATE TABLE `ex_lookup_values` (
  `ROWID` bigint(20) UNSIGNED NOT NULL AUTO_INCREMENT,
  `LOOKUP_TYPE` varchar(30) NOT NULL,
  `LOOKUP_VALUE` varchar(80) NOT NULL,
  `LOOKUP_MEANING` varchar(250) NOT NULL,
  `START_DATE` datetime NOT NULL,
  `END_DATE` datetime DEFAULT NULL,
  PRIMARY KEY  (`ROWID`)
) ENGINE=InnoDB AUTO_INCREMENT=132 DEFAULT CHARSET=latin1;
```

How much do we have to code to get the following features...

  * A search screen with results shown in a nice sortable grid
> ![http://gwtexpress.googlepages.com/gwtExpress-0005.jpg](http://gwtexpress.googlepages.com/gwtExpress-0005.jpg)
  * An advanced search screen with results shown in a nice sortable grid
> ![http://gwtexpress.googlepages.com/adv_search.jpg](http://gwtexpress.googlepages.com/adv_search.jpg)
  * An input form for creating a new lookup value
> ![http://www.gwtexpress.com-a.googlepages.com/gwtExpress-0002.jpg](http://www.gwtexpress.com-a.googlepages.com/gwtExpress-0002.jpg)
  * An input form with complex layout (nested forms with title)
> ![http://gwtexpress.googlepages.com/complex_layout.jpg](http://gwtexpress.googlepages.com/complex_layout.jpg)
  * An edit form for editing the above searched results
> ![http://www.gwtexpress.com-a.googlepages.com/gwtExpress-0004.jpg](http://www.gwtexpress.com-a.googlepages.com/gwtExpress-0004.jpg)
  * Basic client side data validation such as mandatory fields, dates, numbers etc
> ![http://gwtexpress.googlepages.com/gwtExpress-0006.jpg](http://gwtexpress.googlepages.com/gwtExpress-0006.jpg)

Check out the [Demo](http://www.gwtexpress.com) to see how it works...

# Coding options #
**Option 1**
A set of column objects
```
public interface Column {

    public void setColumnName(String columnName);

    public String getColumnName();

    public void setLabel(String label);

    public String getLabel();

    public void setUpdateAllowed(boolean updateAllowed);

    public boolean isUpdateAllowed();

    public void setUpdateOnlyIfNew(boolean updateOnlyIfNew);

    public boolean isUpdateOnlyIfNew();

    public void setVisible(boolean visible);

    public boolean isVisible();

    public void setRequired(boolean required);

    public boolean isRequired();

    public void setQueryAllowed(boolean queryAllowed);

    public boolean isQueryAllowed();

    public void setPrimaryKey(boolean primaryKey);

    public boolean isPrimaryKey();

    public void setRowId(boolean rowId);

    public boolean isRowId();

    public void setDataType(char dataType);

    public char getDataType();

    public void setWidth(int width);

    public int getWidth();

    void setLookupType(String lookupType);

    String getLookupType();

    void setLookupName(String lookupName);

    String getLookupName();

    void setReadOnly(boolean readOnly);

    boolean isReadOnly();
}
```

**Option 2**
An XML file representing the above information...
```
<?xml version="1.0" encoding="UTF-8"?>
<model>
 <tableList>
  <table tableName="customers" title="Customers">
   <columnList>
    <column columnName="customerNumber" dataType="N"
     label="Customer Number" required="true" width="20"/>
    <column columnName="customerName" label="Customer Name"
     required="true" width="50"/>
    <column columnName="contactLastName" label="Contact Last Name" width="50"/>
    <column columnName="contactFirstName" label="Contact First Name" width="50"/>
    <column columnName="phone" label="Phone" width="50"/>
    <column columnName="b_addressLine1" label="Street"
     required="true" width="50"/>
    <column columnName="b_addressLine2" label="Apt#" width="50"/>
    <column columnName="b_city" label="City" required="true" width="50"/>
    <column columnName="b_state" label="State" required="true" width="50"/>
    <column columnName="b_postalCode" label="Zip"
     required="true" width="15"/>
    <column columnName="b_country" label="Country" required="true" width="50"/>
    <column columnName="s_addressLine1" label="Street" width="50"/>
    <column columnName="s_addressLine2" label="Apt#" width="50"/>
    <column columnName="s_city" label="City" width="50"/>
    <column columnName="s_state" label="State" width="50"/>
    <column columnName="s_postalCode" label="Zip" width="15"/>
    <column columnName="s_country" label="Country" width="50"/>
    <column columnName="salesRepEmployeeNumber"
     label="Salesrep Employee Number" width="11"/>
    <column columnName="creditLimit" dataType="N" label="Credit Limit" width="22"/>
    <column columnName="start_date" dataType="T" label="Start Date"
     required="true" width="19"/>
    <column columnName="end_date" dataType="D" label="End Date" width="10"/>
    <column columnName="email_address" label="Email Address" width="80"/>
   </columnList>
  </table>
```
**Option 3**
Metadata of the above information stored in a database table...