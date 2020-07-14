var otc = 0;
var bool = 0;
var regex = "[A-Za-z]";
otc_new = workItem.getCustomField("otc");
if(!!otc_new){   //check if field is empty 
    bool = otc_new.search(regex);   //search for any letters, otc is globally defined
    if(bool == -1){
        workItem.setEnumerationValue("obj_type", "not_physical"); //if no letters --> no_physical object
    }
    else{
        workItem.setEnumerationValue("obj_type", "physical");    //if any letters --> physical object
    }
}


