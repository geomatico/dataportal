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
	globalMetadataPanelTitle : 'Global Metadata',
	converterComboLabel : 'Choose converter',
	converterPanelTitle : 'Converters available',
	/* ~i18n */

	autoDestroy : true,

	globalMetadataPanel : null,
	institutionPanel : null,

	initComponent : function() {

		this.createGlobalMetadataFields();
		this.createInstitutionCombo();
		
		var fileUpload = Ext.create('Ext.form.field.File',  {
			name : 'uploadfile',
			id : 'uploadfile',
			fieldLabel : this.uploadFileLabel,
			msgTarget : 'side',
			allowBlank : false,
			anchor : '100%',
			margin : 2,
			buttonText : this.selectFileButtonText
		});
		
		fileUpload.on({
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
				        fileUpload ];

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
	createInstitutionCombo : function() {
		
		this.institutionPanel = Ext.create('Ext.form.Panel', {
			margin : 2,
			border : false,
			title : this.converterPanelTitle
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

		var comboconverters = Ext.create('Ext.form.ComboBox', {
			margin : 2,
		    fieldLabel: this.converterComboLabel,
		    store: store,
		    name: 'converter',
		    displayField: 'name',
		    valueField: 'name'
		});
		
		comboconverters.on(
				{'change' : function(control, newValue, oldValue){
				    var record = control.findRecord(control.valueField, control.getValue());				
					this.globalMetadataPanel.getComponent(
							'institution').setValue(record.data.institution_realname);
					this.globalMetadataPanel.getComponent(
							'creator_url').setValue(record.data.institution_url);
					this.globalMetadataPanel.getComponent(
							'icos_domain').setValue(record.data.icos_domain);
					(this.getDockedItems('.toolbar')[0]).getComponent('convertButton').setDisabled(false);									
				}, scope : this})
		
		this.institutionPanel.add(comboconverters);

	},

	/**
	 * 
	 */
	createGlobalMetadataFields : function() {
		
		this.globalMetadataPanel = Ext.create('Ext.form.Panel', {
			title : this.globalMetadataPanelTitle,
			border : false,
			padding : 2
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