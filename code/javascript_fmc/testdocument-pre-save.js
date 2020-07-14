//Author:                       Leupold, Julian <julian.leupold@siemens.com>
//Date:                         17.04.2020
//Version:                      1.0
//Description:                  This config includes scripts running for the workitem "testdocument". They should make it easier for the user to configure this type of workitem.


returnvalue = "";
var documentnumber = "";
var otc_type = "";
var dcc = "";
var doctitle = "";
var execute = false;


#include not_save_wo_requiredfields.js
if(execute){
    #include otc_subtype_builder.js //this script splits the combined otc.subtype in two different strings otc code and subtype.
    #include docfile_num_builder.js // this script builds the document name and the document file name
    #include doc_title_from_otc.js //this script automatically sets the doc-title field based on the enum list
    #include title_set_name.js  //this title sets the name of the workitem, after the documentnumber and fiel name is processed
    #include dcc_description_setter.js //this script automatically sets the dcc-description field based on the enum list
    #include non_physical.js    //checks the object type based on the otc code ad sets the field
    #include issued.js //sets the issued for section based on the dcc
}





returnvalue;