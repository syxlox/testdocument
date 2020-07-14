
if(!!workItem.getCustomField("otc_subtype")){   //check if field is empty
    doctitle = workItem.getCustomField("otc_subtype").getProperty("description");   //get the description
    workItem.setCustomField("doc_title", doctitle.toString()); //write value to field
}
