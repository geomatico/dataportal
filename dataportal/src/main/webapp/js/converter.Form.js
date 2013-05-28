/**
 * 
 */
Ext.define('converter.Form', {
	extend : 'Ext.form.Panel',
	
	CONVERT : 'convert',
	UPLOAD : 'upload',

	/* i18n */
	uploadFileLabel : "Upload File",
	selectFileButtonText : "Select File:",
	waitingMessageText : "Waiting...",
	waitingTitleText : "Uploading data...",
	uploadButtonText : "Upload",
	cancelButtonText : 'Cancel',
	convertButtonText : 'Convert',
	/* ~i18n */

	width : 400,

	globalMetadataPanel : Ext.create('Ext.form.Panel', {
		border : false
	}),
	institutionPanel : Ext.create('Ext.form.Panel', {
		margin : 2,
		border : false,
		items : [{
		            xtype : 'fieldcontainer',
		            defaultType : 'radiofield',
		    		layout : 'hbox',
		    		id : 'institutionRadio'
		         }]
	}),

	initComponent : function() {

		this.createGlobalMetadataFields();
		this.createInstitutionButtons();
		
		this.fileUpload = Ext.create('Ext.form.field.File',  {
			name : 'uploadfile',
			id : 'uploadfile',
			fieldLabel : this.uploadFileLabel,
			msgTarget : 'side',
			allowBlank : false,
			anchor : '100%',
			margin : 2,
			buttonText : this.selectFileButtonText
		});
		
		this.fileUpload.on({
			'change' : function(button, value) {
				button.up().getComponent('metadata-panel').setVisible(true);
			}, scope : this });
						
		this.items = [ {
				xtype : 'panel',
				id : 'metadata-panel',
				hidden : true,
				items : [
				         	this.globalMetadataPanel, 
				         	this.institutionPanel
				        ]}, 
				        this.fileUpload ];

		this.buttons = [{
			id : 'convertButton',
			scope: this,
			text : this.convertButtonText,
			disabled : true,
			handler : function() {
//				if (this.isValid()) {
				this.submit({
					url : 'converter',
					method : 'POST',
					scope : this,			
					waitMsg : this.waitingMessageText,
					waitTitle : this.waitingTitleText,
					success : function(form, action) {
						
					},
					failure : function(form, action) {
				        switch (action.failureType) {
				            case Ext.form.action.Action.CLIENT_INVALID:
				                
				                break;
				            case Ext.form.action.Action.CONNECT_FAILURE:
				                
				                break;
				            case Ext.form.action.Action.SERVER_INVALID:
				            	
				            	break;
				        }					
					}
				})
//		} else {
//		// TODO is form is not valid
//	}
			}		
		}, {
			scope : this,
			text : this.cancelButtonText,
			handler : function() {
				this.ownerCt.destroy();
			}
		} ];
		
		this.callParent(arguments);
	},

	/**
	 * 
	 */
	createInstitutionButtons : function() {

		Ext.define('Converter', {
			extend : 'Ext.data.Model',
			fields : [ 'name', 'institution_name', 'institution_realname',
					'institution_url', 'icos_domain' ]
		})

		var store = Ext.create('Ext.data.Store', {
			model : 'Converter',
			proxy : {
				type : 'ajax',
				url : 'converter',
				extraParams : {
					institutionbuttons : true
				},
				reader : {
					type : 'xml',
					record : 'converter',
					root : 'converters'
				}
			}
		})

		Ext.define('converter.Radio', {
			extend : 'Ext.form.field.Radio',
			margin: 20,
			contains : null
		})

		store.load({
			scope : this,
			callback : function(records, operation, success) {
				if (success) {
					for ( var indexRecord in records) {
						var record = records[indexRecord];
						console.log(this);
						this.institutionPanel.getComponent('institutionRadio').add(Ext.create(
								'converter.Radio', {
									boxLabel : record.data.name,
									name : record.data.name,
									scope : this,
									contains: record,
									handler : function(control) {
										this.globalMetadataPanel.getComponent(
												'institution').setValue(control.contains.data.institution_realname);
										this.globalMetadataPanel.getComponent(
												'creator_url').setValue(control.contains.data.institution_url);
										this.globalMetadataPanel.getComponent(
												'icos_domain').setValue(control.contains.data.icos_domain);
										(this.getDockedItems('.toolbar')[0]).getComponent('convertButton').setDisabled(false);
									}
								}))
					}
				}
			}
		});
	},

	/**
	 * 
	 */
	createGlobalMetadataFields : function() {
		Ext.define('GlobalAttribute', {
			extend : 'Ext.data.Model',
			fields : [ 'CFname', 'formname', 'defaultvalue' ]
		})

		var store = Ext.create('Ext.data.Store', {
			model : 'GlobalAttribute',
			proxy : {
				type : 'ajax',
				url : 'xml/globalmetadata.xml',
				reader : {
					type : 'xml',
					record : 'globalattribute',
					root : 'globalmetadata'
				}
			}
		})

		store.load({
			scope : this,
			callback : function(records, operation, success) {
				if (success) {
					for ( var indexRecord in records) {
						var record = records[indexRecord];
						this.globalMetadataPanel.add(Ext.create(
								'Ext.form.field.Text', {
									fieldLabel : record.data.formname,
									emptyText : record.data.defaultvalue,
									margin : 1,
									width : 300,
									id : record.data.CFname,
									name : record.data.CFname
								}))
					}
				}
			}
		});
	}
});