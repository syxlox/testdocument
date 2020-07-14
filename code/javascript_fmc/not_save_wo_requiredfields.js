//if required fields not filled, user cant save

if(!workItem.getCustomField("language"))workItem.setEnumerationValue("language", "lan_en")  //set default value of language to en

if(!((!!workItem.getCustomField("doctype"))
    &&(!!workItem.getCustomField("aspectkey"))
    &&(!!workItem.getCustomField("otc"))
    )) { 
        returnvalue = "please fill in all the required field";
        if(workItem.getCustomField("doctype") == null) returnvalue += "doctype is missing";
        if(workItem.getCustomField("aspectkey") == null) returnvalue += "aspectkey is missing";
        if(workItem.getCustomField("otc") == null) returnvalue += "otc is missing";
    }
    else execute = true;
