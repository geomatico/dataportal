/**
 * 
 */
Ext.define('converter.Form', {
	extend: 'Ext.form.Panel',
	
    /* i18n */
    uploadFileLabel: "Upload File",
    selectFileButtonText: "Select File:",
    waitingMessageText: "Waiting...",
    /* ~i18n */

    width: 600,
    height: 400,
    
    initComponent: function() {
    	
	    this.items = [{
				    	items: [{
					    	xtype: 'panel',
					        height: 300,
					        margin: 2
				        }]
	    			},
	    			{
	    				xtype: 'panel',
	    				items : this.getInstitutionButtons()
	    			},
	    			{
		                xtype: 'filefield',
		                name: 'uploadfile',
		                fieldLabel: this.uploadFileLabel,
		                msgTarget: 'side',
		                allowBlank: false,
		                anchor: '100%',
		                margin: 2,
		                buttonText: this.selectFileButtonText
	    			}],
	    			
		this.buttons = [{
			text: 'Upload',
		    handler: function() {
				if(this.isValid()) {
					this.submit({
						url: 'dataportal/uploadFile',
						waitMsg: this.waitingMessageText,
				        sucess: function(form, action) {
				            			
				        },
				        failure: function(form, action) {
				            			
				        }
				    })
				} else {
					// TODO is form is not valid
				}
		    }},{
		    	text: 'Cancel'
		    }]
    	this.callParent(arguments);
    },
    
    /**
     * 
     */
	getInstitutionButtons: function() {
		
	}
});