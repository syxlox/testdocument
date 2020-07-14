
if(workItem.getTitle().search("skip") == -1){	//if in the title the control word "skip" is written in the title, no title will be set from here
	if(!!workItem.getCustomField("documentn")) documentnumber = doctitle + ",  " + documentnumber;
	else documentnumber = doctitle;
	workItem.setTitle(documentnumber); 
   }