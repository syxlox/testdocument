var otcwsubtype = "";
var splits = ["", ""];

otcwsubtype = workItem.getCustomField("otc").toLowerCase();
if(!!workItem.getCustomField("subtype")) otcwsubtype += "_" + workItem.getCustomField("subtype").toLowerCase().slice(1);    //get the 2 parts and build one otc.subtype

workItem.setEnumerationValue("otc_subtype", otcwsubtype);
if(workItem.getCustomField("otc_subtype").getName() == null) returnvalue = otwcsubtype + "not existing atm; maybe create a new one or check for typo";


