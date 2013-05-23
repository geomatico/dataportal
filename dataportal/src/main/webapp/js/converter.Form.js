/**
 * 
 */
Ext.define('converter.Form', {
	extend : 'Ext.form.Panel',

	/* i18n */
	uploadFileLabel : "Upload File",
	selectFileButtonText : "Select File:",
	waitingMessageText : "Waiting...",
	uploadButtonText : "Upload",
	cancelButtonText : 'Cancel',
	convertButtonText : 'Convert',
	/* ~i18n */

	width : 400,

	globalMetadataPanel : Ext.create('Ext.panel.Panel', {
		border : false
	}),
	institutionPanel : Ext.create('Ext.panel.Panel', {
		margin : 2,
		border : false
	}),

	initComponent : function() {

		this.createGlobalMetadataFields();
		this.createInstitutionButtons();
						
		this.items = [ {

									xtype : 'panel',
									id : 'metadata-panel',
									hidden : true,
									items : [
											 this.globalMetadataPanel, 
								               this.institutionPanel
									         ]
								}, {
									xtype : 'filefield',
									name : 'uploadfile',
									id : 'uploadfile',
									fieldLabel : this.uploadFileLabel,
									msgTarget : 'side',
									allowBlank : false,
									anchor : '100%',
									margin : 2,
									buttonText : this.selectFileButtonText
								} ],

		this.buttons = [ {
			id : 'uploadButton',
			scope: this,
			text : this.uploadButtonText,
			handler : function() {
					//		if (this.isValid()) {
					this.submit({
						url : 'converter',
						scope : this,
						method : 'POST',
						waitMsg : this.waitingMessageText,
						success : function(form, action) {
							this.getComponent('metadata-panel').setVisible(true);
							this.getComponent('uploadfile').setVisible(false);
							(this.getDockedItems('.toolbar')[0]).getComponent('convertButton').setVisible(true);
							(this.getDockedItems('.toolbar')[0]).getComponent('uploadButton').setVisible(false);
						},
						failure : function(form, action) {
					        switch (action.failureType) {
					            case Ext.form.action.Action.CLIENT_INVALID:
					                
					                break;
					            case Ext.form.action.Action.CONNECT_FAILURE:
					                
					                break;
					            case Ext.form.action.Action.SERVER_INVALID:
					            	
					        }
						}
					})
			//	} else {
			//		// TODO is form is not valid
			//	}
			}
		}, {
			id : 'convertButton',
			scope: this,
			text : this.convertButtonText,
			hidden : true,
			disabled : true,
			handler : function() {
				
			}		
		}, {
			scope : this,
			text : this.cancelButtonText,
			handler : function() {
				this.ownerCt.destroy();
			}
		} ]
		
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
					converters : true
				},
				reader : {
					type : 'xml',
					record : 'converter',
					root : 'converters'
				}
			}
		})

		Ext.define('converter.Button', {
			extend : 'Ext.Button',
			height : 60,
			margin : 2,
			contains : null
		})

		store.load({
			scope : this,
			callback : function(records, operation, success) {
				if (success) {
					for ( var indexRecord in records) {
						var record = records[indexRecord];
						this.institutionPanel.add(Ext.create(
								'converter.Button', {
									text : record.data.name,
									enableToggle : true,
									toggleGroup : 'converters',
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
									id : record.data.CFname
								}))
					}
				}
			}
		});
	}
});