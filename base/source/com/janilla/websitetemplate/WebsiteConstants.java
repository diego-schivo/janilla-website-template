package com.janilla.websitetemplate;

import com.janilla.blanktemplate.BlankConstants;

public class WebsiteConstants extends BlankConstants {

	public Page emptyPage() {
		return PageImpl.EMPTY;
	}

	public FormConfirmationType messageFormConfirmationType() {
		return FormConfirmationTypeImpl.MESSAGE;
	}
}
