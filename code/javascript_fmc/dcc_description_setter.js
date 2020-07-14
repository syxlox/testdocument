var description = 0;
if(!!workItem.getCustomField("dcc_class")){     //check if empty
    description = workItem.getCustomField("dcc_class").getProperty("description");      //get description
    workItem.setCustomField("dcc_description", description.toString());     //set value to field
}