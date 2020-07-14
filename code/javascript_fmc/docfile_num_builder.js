

/*
tis script builds the refblock and the file name of the doc 
sometimes its possible to run through them in an array because the field type is the same.
most of the time the seperator is not the same, so we need extra ifs here
i splitted them because i think its better to overview
*/


var refblock = "";
var parts = ["p_levelb", "p_levelc", "p_leveld", "p_levele", "p_levelf", "p_levelg", "p_levelh"]; //levels 
var parts_two = ["code_letter", "dcc_class"];
var counter = 0;
var result = "";
var docfile = "";
var refblock = "";
           

docnumber = "_" + workItem.getCustomField("doctype").getName() + workItem.getCustomField("aspectkey").getName() + "_"; //no check because mandatory field


if(!!workItem.getCustomField("otc_subtype")) docnumber += workItem.getCustomField("otc_subtype").getName();

if(!!workItem.getCustomField("p_stationn")) docnumber += workItem.getCustomField("p_stationn");

block_1:{
    for (i = 0;i < parts.length; i++) {                                              
        if(!workItem.getCustomField(parts[i])) break block_1;           //array because all string fields
        else refblock += workItem.getCustomField(parts[i]);
    }
}


for(i = 0; i < parts_two.length; i++){

    if(!!workItem.getCustomField(parts_two[i])) refblock += workItem.getCustomField(parts_two[i]).getName();   //build futher, array because of 2 times enum field, no check because mandatory field

}

if(!!workItem.getCustomField("page_counter")) refblock += ("/" + workItem.getCustomField("page_counter"));
else refblock += "";

if(!!workItem.getCustomField("language")) refblock += ("_" + workItem.getCustomField("language").getName());
else refblock += "";

if(!!workItem.getCustomField("revision")) refblock += ("_" + workItem.getCustomField("revision"));
else refblock += "";

result = workItem.getCustomField("projectid") + docnumber + refblock;
documentnumber = result;

if(!!workItem.getCustomField(parts_two[1])) dcc = workItem.getCustomField(parts_two[1]).getName();  //save dcc for later

workItem.setCustomField("documentn", result); 

docfile = refblock.replace(/[\+ \- \=]+/g,'');
docfile = docfile.replace(/&amp;/g, '&');
docfile = docfile.replace(/[\. \# \/ \&]+/g,"_");

docnumber = docnumber.replace(/[\+ \- \=]+/g,'');
docnumber = docnumber.replace(/&amp;/g, '&');
docnumber = docnumber.replace(/[\. \# \/ \&]+/g,"_");

docfile = workItem.getCustomField("projectid") + docnumber + "_" + docfile;


workItem.setCustomField("doc_file", docfile);



