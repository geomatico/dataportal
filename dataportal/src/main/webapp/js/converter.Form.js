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
	failureMessageTitle : 'Error!',
	successMessageTitle : 'Success!',
	successMessageText : 'Upload data and generated NC File with ID: ',
	inValidFormText : 'Form not valid!',
	/* ~i18n */

	autoDestroy : true,

	globalMetadataPanel : null,
	institutionPanel : null,

	initComponent : function() {

		this.createGlobalMetadataFields();
		this.createInstitutionRadios();
		
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
				if (this.getForm().isValid()) {
					this.getForm().submit({
						url : 'converter',
						method : 'POST',
						scope : this,			
						waitMsg : this.waitingMessageText,
						waitTitle : this.waitingTitleText,
						success : function(form, action) {
							Ext.MessageBox.show({
								title: this.successMessageTitle,
								msg: this.successMessageText + action.result.msg,
								buttons: Ext.MessageBox.OK,
								scope : this,
								fn : function(button) {
									this.ownerCt.destroy();
									this.destroy()
								}
							});
						},
						failure : function(form, action) {
							Ext.MessageBox.show({
									title: this.failureMessageTitle,
									msg: action.result.msg || action.response.responseText,
									buttons: Ext.MessageBox.OK,
									icon: Ext.MessageBox.ERROR
								});
						}
					})
				} else {
					Ext.MessageBox.show({
						title: this.failureMessageTitle,
						msg: this.inValidFormText,
						buttons: Ext.MessageBox.OK,
						icon: Ext.MessageBox.ERROR
					});
				}
			}		
		}, {
			scope : this,
			text : this.cancelButtonText,
			handler : function() {
				this.ownerCt.destroy();
				this.destroy()
			}
		} ];
		
		this.callParent(arguments);
	},

	/**
	 * 
	 */
	createInstitutionRadios : function() {
		
		this.institutionPanel = Ext.create('Ext.form.Panel', {
			margin : 2,
			border : false,
			items : [{
			            xtype : 'fieldcontainer',
			            defaultType : 'radiofield',
			    		layout : 'hbox',
			    		id : 'institutionRadio'
			         }]
		});

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
		
		this.globalMetadataPanel = Ext.create('Ext.form.Panel', {
			border : false
		});
		
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