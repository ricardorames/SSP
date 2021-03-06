/*
 * Licensed to Apereo under one or more contributor license
 * agreements. See the NOTICE file distributed with this work
 * for additional information regarding copyright ownership.
 * Apereo licenses this file to you under the Apache License,
 * Version 2.0 (the "License"); you may not use this file
 * except in compliance with the License.  You may obtain a
 * copy of the License at the following location:
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */
Ext.define("Ssp.view.tools.profile.Watchers", {
    extend: "Ext.grid.Panel",
    alias: "widget.profilewatchers",
    mixins: ["Deft.mixin.Controllable", 'Deft.mixin.Injectable'],
    controller: "Ssp.controller.tool.profile.ProfileWatchersViewController",
    width: "100%",
    height: "100%",
    title: "Current Watchers for this Student",
    autoScroll: true,
    sortableColumns: true,
    inject: {
        store: 'watchStudentListStore',
        textStore: 'sspTextStore'
    },
    initComponent: function() {
        var me = this;
        Ext.applyIf(me, {
            queryMode: "local",
            store: me.store,
            xtype: "gridcolumn",
            columns: [{
                dataIndex: "firstName",
                text: me.textStore.getValueByCode('ssp.label.first-name',"First Name"),
                flex: 1
            }, {
                dataIndex: "lastName",
                text: me.textStore.getValueByCode('ssp.label.last-name',"Last Name"),
                flex: 1
            }, {
                dataIndex: "primaryEmailAddress",
                text: me.textStore.getValueByCode('ssp.label.main.watchers.primary-email-address',"Email"),
                flex: 1
            }]
        });
        me.callParent(arguments);
    }
});
