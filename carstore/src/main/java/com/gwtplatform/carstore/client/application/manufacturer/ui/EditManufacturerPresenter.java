/*
 * Copyright 2013 ArcBees Inc.
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not
 * use this file except in compliance with the License. You may obtain a copy of
 * the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the
 * License for the specific language governing permissions and limitations under
 * the License.
 */

package com.gwtplatform.carstore.client.application.manufacturer.ui;

import javax.inject.Inject;

import com.google.web.bindery.event.shared.EventBus;
import com.gwtplatform.carstore.client.application.event.DisplayMessageEvent;
import com.gwtplatform.carstore.client.application.manufacturer.event.ManufacturerAddedEvent;
import com.gwtplatform.carstore.client.application.manufacturer.ui.EditManufacturerPresenter.MyView;
import com.gwtplatform.carstore.client.application.widget.message.Message;
import com.gwtplatform.carstore.client.application.widget.message.MessageStyle;
import com.gwtplatform.carstore.client.resources.EditManufacturerMessages;
import com.gwtplatform.carstore.client.util.ErrorHandlerAsyncCallback;
import com.gwtplatform.carstore.shared.api.ManufacturersResource;
import com.gwtplatform.carstore.shared.dto.ManufacturerDto;
import com.gwtplatform.dispatch.rest.delegates.client.ResourceDelegate;
import com.gwtplatform.mvp.client.HasUiHandlers;
import com.gwtplatform.mvp.client.PopupView;
import com.gwtplatform.mvp.client.PresenterWidget;
import com.gwtplatform.mvp.client.proxy.RevealRootPopupContentEvent;

public class EditManufacturerPresenter extends PresenterWidget<MyView> implements EditManufacturerUiHandlers {
    public interface MyView extends PopupView, HasUiHandlers<EditManufacturerUiHandlers> {
        void edit(ManufacturerDto manufacturerDto);
    }

    private final ResourceDelegate<ManufacturersResource> manufacturersDelegate;
    private final EditManufacturerMessages messages;

    private ManufacturerDto manufacturerDto;

    @Inject
    EditManufacturerPresenter(
            EventBus eventBus,
            MyView view,
            ResourceDelegate<ManufacturersResource> manufacturerResourceDelegate,
            EditManufacturerMessages messages) {
        super(eventBus, view);

        this.manufacturersDelegate = manufacturerResourceDelegate;
        this.messages = messages;

        getView().setUiHandlers(this);
    }

    @Override
    public void createNew() {
        manufacturerDto = new ManufacturerDto();

        reveal();
    }

    @Override
    public void onCancel() {
        getView().hide();
    }

    @Override
    public void edit(ManufacturerDto manufacturerDto) {
        this.manufacturerDto = manufacturerDto;

        reveal();
    }

    @Override
    public void onSave(ManufacturerDto manufacturerDto) {
        manufacturersDelegate
                .withCallback(new ErrorHandlerAsyncCallback<ManufacturerDto>(this) {
                    @Override
                    public void onSuccess(ManufacturerDto savedManufacturer) {
                        DisplayMessageEvent.fire(EditManufacturerPresenter.this,
                                new Message(messages.manufacturerSaved(), MessageStyle.SUCCESS));
                        ManufacturerAddedEvent.fire(EditManufacturerPresenter.this, savedManufacturer);

                        getView().hide();
                    }
                })
                .saveOrCreate(manufacturerDto);
    }

    private void reveal() {
        getView().edit(manufacturerDto);

        RevealRootPopupContentEvent.fire(this, this);
    }
}
