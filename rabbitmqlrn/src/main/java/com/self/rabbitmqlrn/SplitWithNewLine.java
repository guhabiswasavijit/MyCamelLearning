package com.self.rabbitmqlrn;

import java.util.Arrays;
import java.util.List;

public class SplitWithNewLine {
	
	public List<String> splitRecords(String body){
		if(body != null) {
			String[] records = body.split("\n");
			String[] recordsAfterIstLine = new String[records.length - 1];
			System.arraycopy(records,1,recordsAfterIstLine, 0,recordsAfterIstLine.length);
			List<String> recordList = Arrays.asList(recordsAfterIstLine);
			return recordList;
		}
		return null;
	}

}
