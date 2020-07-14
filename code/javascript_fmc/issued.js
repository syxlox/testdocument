var torun = ["ifi", "ifa", "ifd", "ifm", "ifc", "ab"];
var tocheck = ["DC08", "DC03", "DC18"];

for(i = 0; i < torun.length; i++){      //set all issued on no
    workItem.setEnumerationValue(torun[i], "no");
}

if(!!workItem.getCustomField("dcc_class")){ //if dcc not empty..; check the dcc for specific dccs; then set the issued for section based on this logic
    workItem.setDescription("");
    var dcc = workItem.getCustomField("dcc_class").getName();   
    block_1:{
    if(dcc.search("QC05") != -1){
        workItem.setEnumerationValue("ab", "yes");
        break block_1;
    }
    block_2:{
    for(i = 0; i < tocheck.length; i++){
        if(dcc.search(tocheck[i]) != -1){
            workItem.setEnumerationValue("ifa", "yes");
            break block_2;
        }
    }
    }
    if(dcc.search("QC04") != -1 || dcc.search("QC01") != -1) workItem.setEnumerationValue("ifi", "yes");
    }
} 