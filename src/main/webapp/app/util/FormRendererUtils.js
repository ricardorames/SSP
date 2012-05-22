Ext.define('Ssp.util.FormRendererUtils',{	
	extend: 'Ext.Component',
    mixins: [ 'Deft.mixin.Injectable'],
    inject: {
        appEventsController: 'appEventsController'
    },
	config: {
		additionalFieldsKeySeparator: '_'
	},
	initComponent: function() {
		return this.callParent(arguments);
    },
    
    constructor: function(){
    	this.callParent(arguments);
    	return this;
    },
	
    cleanAll: function(view){
    	if (view.items)
    	{
        	if (view.items.length > 0)
        	{
        		view.removeAll(true);
        	}	
    	}
    },
    
    cleanItems: function(view){
    	var i = view.items.length;
    	while (view.items.length > 0)
    	{
    		var item = view.items.getAt(i);
    		if (item != undefined)
    			view.remove(item, true);
    		i=i-1;
    	}
    },	
	
    getProfileFormItems: function(){
		var cleaner = Ext.create( 'Ssp.util.TemplateDataUtil' );
		var applicationFormsStore =  Ext.getStore('ApplicationForms');
        return cleaner.prepareTemplateData(  applicationFormsStore );  	
    },    
    
    /**
     * Builds a dynamic group of radio buttons.
     * @args
     *  @formId 
     *  @radioGroupId
     *  @itemsArr
     *  @selectedItemId
     *  @idFieldName
     *  @selectedIdFieldName
     */
    createRadioButtonGroup: function( args ){
    	var formId = args.formId;
    	var radioGroupId = args.radioGroupId;
    	var selectedItemId = args.selectedItemId;
    	var additionalFieldsMap = args.additionalFieldsMap;
    	var itemsArr = args.itemsArr;
    	var idFieldName = args.idFieldName;
    	var selectedIdFieldName = args.selectedIdFieldName;
    	
    	var form = Ext.getCmp(formId);
    	var rbGroup = Ext.getCmp(radioGroupId);
		var items = itemsArr;
		var setSelectedItems = false;
		for (i=0; i<items.length; i++)
		{
			var comp = {xtype:'radio'};
			var item = items[i];
			comp.boxLabel = item.name;
			comp.name = selectedIdFieldName;
			comp.inputValue = item[idFieldName];
			
			// loop through additional fields map and add fields to the form
			var fieldsArr = [];
			fieldsArr.push( comp );
			
			additionalFieldsArr = this.getMappedFields( selectedItemId, additionalFieldsMap );
			
	    	// add a fieldset if additional fields exist for this item
	    	if (additionalFieldsArr.length>0)
	    	{
		    	var fields = {xtype: 'fieldset', padding: 0, border: 0, layout: { type: 'auto' },title: ''};
		    	Ext.Array.insert(fieldsArr, 1, additionalFieldsArr);
		    	Ext.apply(fields, {items: fieldsArr});
	    	}
	    	
			if (selectedItemId==item[idFieldName])
			{
				comp.checked = true;
			}

			// if a fieldset is not defined, then just return a checkbox
			if (fieldsArr.length > 1)
			{
				rbGroup.add(fields);
			}else{
				rbGroup.add(comp);
			}
		}
		form.doLayout();
    },
    
    /**
     * Provides the ability to build a form with a dynamic
     * set of elements. (checkboxes or radio)
     * 
     * Assumes an items array with: {id: id, name: name, description: description}
     * @args
     *    @mainComponentType = the main component type for the form ('checkbox', 'radio') 
     *    @radioGroupId = the radiobuttongroup id value / optional if building a checkbox based form
     *    @selectedItemId - required value for the 'radio' select button id and optional for 'checkbox' mainComponentType  
     *    @formId = the form to add the items into
     *    @fieldSetTitle - Provides a fieldset title for the fields
     *    @itemsArr = the array of items to add to the form
     *    @selectedItemsArr = the array of items to select in the form
     *    @idFieldName = the id field in the items array
     *    @selectedIdFieldName - the id field in the selectedItems array. 
     *                        This value can be the same name or a different name than the idFieldName.
     *    @additionalFieldsMap - a series of fields to provide for description related field items
     */
    createForm: function( args ){
    	var me=this;
    	var mainComponentType = args.mainComponentType;
    	var formId = args.formId;
    	var fieldSetTitle = args.fieldSetTitle || null;
    	var itemsArr = args.itemsArr;
    	var selectedItemsArr = args.selectedItemsArr || null;
    	var idFieldName = args.idFieldName;
    	var selectedIdFieldName = args.selectedIdFieldName;
    	var additionalFieldsMap = args.additionalFieldsMap || [];
    	var form = Ext.getCmp(formId);
		var selectedItems = selectedItemsArr;
		var selectedItem;
		var selectedId;
		var setSelectedItems = false;
		var otherId = "";
		var wrapper = {xtype: 'fieldset', padding: 0, border: 0, layout: { type: 'auto' },title: fieldSetTitle};
		var formFields = [];
		var selectedItems = selectedItemsArr || [];
		
		if ( mainComponentType == 'radio' )
		{
			this.createRadioButtonGroup(args);
		}else{
			Ext.each(itemsArr, function(item, index){
				var itemId = item[idFieldName];
				// create the items for the form
				var comp = {xtype: 'mappedcheckbox'};
				comp.id = itemId;
				comp.mapId = itemId;
				comp.boxLabel = item.name;
				comp.name = item.name;
				comp.inputValue = itemId;
				comp.listeners = {
					change: function(comp, oldValue, newValue, eOpts){
						me.appEventsController.getApplication().fireEvent('dynamicCompChange', comp);
					}	
				};

				// loop through additional fields map and add description type fields to the form
				var fieldsArr = [];
				fieldsArr.push( comp );
				
				additionalFieldsArr = me.getMappedFields( itemId, additionalFieldsMap );				
				
				// determine if the component is selected
				for (var s=0; s<selectedItems.length; s++)
				{
					selectedItem = selectedItems[s]
					selectedId = selectedItem[selectedIdFieldName];					
					if (selectedId==item[idFieldName])
					{
						comp.checked = true;					
						
						// populate the additional fields with selected values
						for(var z=0; z<additionalFieldsArr.length; z++)
						{
							var field = additionalFieldsArr[z];
							var names = field.name.split( me.additionalFieldsKeySeparator );
							if ( field.parentId == selectedId )
							{
								field.setValue( selectedItem[names[1]] );
							}else{
								field.setValue("");
							}
						}							

						break;
					}
				}
				
				this.hideEmptyFields(additionalFieldsArr);
				
		    	// add a fieldset if additional fields exist for this item
		    	if (additionalFieldsArr.length>0)
		    	{
			    	var fields = {xtype: 'fieldset', padding: 0, border: 0, layout: { type: 'auto' },title: ''};
			    	Ext.Array.insert(fieldsArr, 1, additionalFieldsArr);
			    	Ext.apply(fields, {items: fieldsArr});
		    	}
				
				// if a fieldset is not defined, then just return a checkbox
				if (fieldsArr.length > 1)
				{
					formFields.push(fields);
				}else{
					formFields.push( comp );
				}
				
			}, me);
			
			form.removeAll();
			Ext.apply(wrapper, {items: formFields});
			form.insert(form.items.length, wrapper);
		}
    },
    
    /**
     * Hides fields with no value set.
     */
    hideEmptyFields: function( arr ){
    	Ext.each(arr, function(item, index){
    		if (item.getValue()=="")
    		{
    			item.hide();
    			Ext.apply(item,{allowBlank:true});
    		}else{
    			item.show();
    			Ext.apply(item,{allowBlank:false});
    		}
    	});
    },
    
    /**
     * Loops over a collection of field maps and 
     * returns an array of fields
     */
    getMappedFields: function( itemId, maps ){
		var additionalFieldsArr = [];
    	for (var z=0; z<maps.length; z++)
		{
			var map = maps[z];
			if ( itemId == map.parentId )
			{
				var field = this.getFieldFromMap( map );
				additionalFieldsArr.push( field );
			}
		}

    	return additionalFieldsArr;
    },
    
    /**
     * returns a field based on the parameters
     * in the map.
     * @param map object
     *  fieldType
     *  name
     *  parentId
     *  label
     *  labelWidth
     *  
     *  Example:
     *  {parentId: '365e8c95-f356-4f1f-8d79-4771ae8b0291',
	 *   parentName: "other",
	 *   name: "otherDescription", 
	 *   label: "Please Explain", 
     *   fieldType: "textarea",
     *   labelWidth: defaultLabelWidth}
     * 
     * @returns a field created by it's xtype
     */
    getFieldFromMap: function( map ){
    	var field = Ext.createWidget( map.fieldType.toLowerCase() );
    	var valErrorText = 'Not a valid input.';
    	var validationExpression=field.validationExpression;
    	if (map.validationExpression != null)
    		validationExpression=map.validationExpression;
    	if (map.validationErrorMessage != null)
    		valErrorText = map.validationErrorMessage;
    	
    	console.log(validationExpression);
    	Ext.apply(field, {
    		parentId: map.parentId, 
    		name: map.parentId + this.additionalFieldsKeySeparator + map.name, 
    		fieldLabel: map.label, 
    		labelWidth: map.labelWidth,
    		anchor: '100%',
    		vtype: 'mappedFieldValidator',
    		vtypeText: valErrorText,
    		validationExpression: validationExpression
    	});    	
    	
    	// This field get's hidden when it's parent
    	// is not selected
    	field.on("hide", 
    		function(comp, eOpts){
    			comp.setValue("");
    	});
    	

    	return field;
    },
    
    /**
     * Determines if the string is using
     * the additionalFields model based on
     * a separator for the field.
     */
    isAdditionalFieldKey: function( key ){
    	var isKey = (key.indexOf( this.additionalFieldsKeySeparator ) != -1);
    	return isKey;
    },
    
    /**
     * @param obj - a values object from a form
     * @returns array of additionalField objects with name/value pairs
     */
    getAdditionalFieldsArrayFromFormValues: function( obj ){
    	var fields = [];
    	Ext.iterate(obj, function(key, value) {
			if ( this.isAdditionalFieldKey( key ) )
			{
				keys = key.split( this.additionalFieldsKeySeparator );
				fields.push( {id: keys[0], name: keys[1], value: value} );
			} 
		},this);
    	return fields;
    },
    
    /**
     * @param obj - a values object from a form
     * @returns the object clean of any keys that the signature
     *          of an additional form field. ie. a description field
     *          for one of the items in the form.
     */
    dropAdditionalFieldsKeysFromFormValues: function( obj ){
    	Ext.iterate(obj, function(key, value) {
			if ( this.isAdditionalFieldKey( key ) )
			{
				delete obj[key];
			} 
		},this);
    	return obj;
    },
    
    /**
     * Method to create a json transfer object from
     * the selected values in a form.
     * This method is specifically for use with the
     * AdditionalFieldMappings related object type,
     * for dynamic check and radio objects.
     * @idKey = the supplied name of the key field in the transfer object
     * @formValues = an object containing key value pairs from the form
     * @personId = a related key value for the object
     * 
     * @returns = an array of transfer objects for the selected items in the form
     */
	createTransferObjectFromSelectedValues: function(idKey, formValues, personId){
		var transferObjects = [];	
		var formUtils = this.formUtils;
		var additionalFieldArr = [];
		
		// Loop through all the values in the form
		// find the objects with an '_' character and save them to a new array
		additionalFieldsArr = this.getAdditionalFieldsArrayFromFormValues( formValues );

		// delete keys that match an additional fields signature, since they will be used to determine mapped description fields
		// and not actual selected items
		// compare the values in each of the keys against the selected items to create 
		// a series of personEducationLevel objects to save
		
		formValues = this.dropAdditionalFieldsKeysFromFormValues( formValues );
		
		// Loop through all the values in the form and create
		// transfer objects for them with an id field name matching
		// the supplied idKey. For each transfer object, loop over the
		// the additionalFields and match the id's to determine
		// additional fields that should be supplied as descriptions
		// against the mapped fields
		Ext.iterate(formValues, function(key, value) {
			var tObj = new Object();
			tObj[idKey]=value;
			tObj['personId'] = personId;
			Ext.Array.each( additionalFieldsArr, function(field, index){
				if (value==field.id)
					tObj[field.name]=field.value;
			}, this);
			transferObjects.push( tObj );
		});
		
		return transferObjects;
	},    
    
	/**
	 * Allows an additional field to be hidden until
	 * an item is selected that is associated with the 
	 * hidden field.
	 */
    onMapFieldHidden: function( comp, eOpts){
    	comp.setValue("");
    },
	
	/**
	 * @params
	 * @arrayToSort - the array to sort props on
	 * @fieldName - the field name to sort on
	 * 
	 * @returns - returns the sorted array
	 */
    alphaSortByField: function( arrayToSort, fieldName ){
    	return Ext.Array.sort(arrayToSort, function(a, b){
    		 var nameA=a[fieldName].toLowerCase(), nameB=b[fieldName].toLowerCase()
    		 if (nameA < nameB) //sort string ascending
    		  return -1
    		 if (nameA > nameB)
    		  return 1
    		 return 0 //default return value (no sorting)
    		});
    },
    
    /**
     * @params
     * @values - an object of name/value pairs
     * @modelType - the model type to set and return
     * 
     * @returns - a typed model object
     */
    getSelectedValuesAsTransferObject: function( values, modelType ){
		var selectedItems = [];
		for ( prop in values )
		{
			var obj = Ext.create(modelType,{id: values[prop]} );
			selectedItems.push( obj.data );
		}
		return selectedItems;
    },
    
 	/**
	 * load a display into a container in the interface.
	 * @containerAlias = alias/xtype of the container into which to load the display.
	 * @compAlias = alias/xtype of the component that is loaded
	 * @removeExisting = boolean: true=remove existing items before load, 
	 *                            false=keep existing items
	 * @args = optional params space
	 */	
	loadDisplay: function( containerAlias, compAlias, removeExisting, args ) {	
		var comp = null;
		var store = null;
		var view = Ext.ComponentQuery.query(containerAlias.toLowerCase())[0];
		
		if (view.items.length > 0 && removeExisting==true)
			view.removeAll();

		// create the new widget
		comp =  Ext.createWidget(compAlias.toLowerCase());	
		
		// add to the container
		view.add( comp );
		
		return comp;
	},

    /**
     * Compares a value against a record in a store, based on a provided
     * field for the comparison, as well as, criteria to find the record
     * in the store to use to compare against. If the values match
     * then the associated field will be hidden in the interface. Otherwise,
     * the field is shown.
     * @elementName - the field to hide
     * @compareValue - the value to compare against
     * @compareFieldName - the field in a record to compare against. For example: 'id'
     * @store - the store in which to find a value to compare against
     * @recordField - the name of the field in the store to find a record
     * @recordValue - the value of the field in the store to find a record against
     */
    showHideFieldByStoreValue: function( elementName, compareValue, compareFieldName, store, recordField, recordValue ){
		var queryValue = '#'+elementName;
    	var field = Ext.ComponentQuery.query(queryValue)[0];
		var record = store.findRecord(recordField, recordValue);
		if ( compareValue==record.get( compareFieldName ) )
		{
			field.show();
		}else{
			field.hide();
		}
    }    
    
});

