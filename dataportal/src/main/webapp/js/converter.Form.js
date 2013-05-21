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
	/* ~i18n */

	width : 600,
	height : 400,
	getInstitutionButtonsItems : [],

	globalMetadataPanel : Ext.create('Ext.panel.Panel'),
	institutionPanel : Ext.create('Ext.panel.Panel'),
	setConverterType : null,

	initComponent : function() {

		this.createInstitutionButtons();

		this.items = [ this.globalMetadataPanel, this.institutionPanel, {
			xtype : 'filefield',
			name : 'uploadfile',
			fieldLabel : this.uploadFileLabel,
			msgTarget : 'side',
			allowBlank : false,
			anchor : '100%',
			margin : 2,
			buttonText : this.selectFileButtonText
		} ],

		this.buttons = [ {
			text : this.uploadButtonText,
			handler : function() {
				if (this.isValid()) {
					this.submit({
						url : 'dataportal/uploadFile',
						waitMsg : this.waitingMessageText,
						sucess : function(form, action) {

						},
						failure : function(form, action) {

						}
					})
				} else {
					// TODO is form is not valid
				}
			}
		}, {
			text : this.cancelButtonText
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
					'institution_url' ]
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

		store.load({
			scope : this,
			callback : function(records, operation, success) {
				if (success) {
					for ( var indexRecord in records) {
						var record = records[indexRecord];
						this.institutionPanel.add(Ext.create('Ext.Button', {
							height : 60,
							margin : 2,
							text : record.data.name,
							enableToggle : true,
							toggleGroup : 'converters',
							scope : this,
							handler : function(control) {
								this.setConverterType = control.text;
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
		
	}
});