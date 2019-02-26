package com.crestron.blackbird.mobile.persistence.listener;

import android.content.Context;
import android.support.annotation.VisibleForTesting;

import com.crestron.blackbird.mobile.cssettings.interactor.DbInteractorCsSettings;
import com.crestron.blackbird.mobile.cssettings.model.EntryModel;
import com.crestron.itemattribute.db.tables.AttributeEntity;
import com.crestron.itemattribute.db.tables.ItemEntity;
import com.crestron.mobile.android.common.RxBus;
import com.crestron.mobile.android.common.rx.RxListenerBase;
import com.crestron.mobile.cssettings.CSPersistenceControlSystemEntryOpUseCase;
import com.crestron.mobile.cssettings.CSPersistenceCreateControlSystemEntryUseCase;
import com.crestron.mobile.cssettings.CSPersistenceDeleteControlSystemEntryUseCase;
import com.crestron.mobile.cssettings.CSPersistenceGetControlSystemEntryUseCase;
import com.crestron.mobile.cssettings.CSPersistenceGetControlSystemsEntriesUseCase;
import com.crestron.mobile.cssettings.CSPersistenceUpdateControlSystemEntryUseCase;
import com.crestron.mobile.cssettings.model.ControlSystemEntry;

import java.util.ArrayList;
import java.util.List;

import io.reactivex.CompletableObserver;
import io.reactivex.MaybeObserver;
import io.reactivex.disposables.Disposable;
import io.reactivex.schedulers.Schedulers;

/**
 * Listens on RxBus for useCases to control settings persistence layer.
 */
public class RxListenerCsSettings implements RxListenerBase {
    private final DbInteractorCsSettings databaseInteractor;

    public RxListenerCsSettings(Context context) {
        databaseInteractor = new DbInteractorCsSettings(context);
    }

    /**
     * Listen on events from RxBus.
     */
    @Override
    public void startListening() {
        //Read
        RxBus.INSTANCE
                .listen(CSPersistenceGetControlSystemEntryUseCase.Request.class)
                .subscribe(request -> {
                    getEntryById(Long.valueOf(request.getControlSystemID()));
                });

        //Read multiple
        RxBus.INSTANCE
                .listen(CSPersistenceGetControlSystemsEntriesUseCase.Request.class)
                .subscribe(request -> {
                    getEntries();
                });

        //Create
        RxBus.INSTANCE
                .listen(CSPersistenceCreateControlSystemEntryUseCase.Request.class)
                .subscribe(
                        request -> {
                            createEntry(request.getCaControlSystems().get(0));
                        });

        //Update
        RxBus.INSTANCE
                .listen(CSPersistenceUpdateControlSystemEntryUseCase.Request.class)
                .subscribe(request -> {
                    createEntry(request.getCaControlSystems().get(0));
                });

        //Delete
        RxBus.INSTANCE
                .listen(CSPersistenceDeleteControlSystemEntryUseCase.Request.class)
                .subscribe(request -> {
                    List<ControlSystemEntry> listOfEntries = request.getCaControlSystems();
                    List<Long> deleteIds = new ArrayList<>();
                    // Delete all entries for sysIds
                    for (ControlSystemEntry entry : listOfEntries) {
                        deleteIds.add(Long.valueOf(entry.getControlSystemID()));
                    }
                    deleteEntries(deleteIds.toArray(new Long[deleteIds.size()]));
                });
    }

    /**
     * Read single control sys.
     *
     * @param sysId Long
     */
    private void getEntryById(Long sysId) {
        databaseInteractor.getSystemById(sysId)
                .subscribeOn(Schedulers.io())
                .subscribe(new MaybeObserver<List<AttributeEntity>>() {
                    @Override
                    public void onSubscribe(Disposable d) {
                        //Send in progress status
                        CSPersistenceGetControlSystemEntryUseCase useCase = new CSPersistenceGetControlSystemEntryUseCase();
                        CSPersistenceGetControlSystemEntryUseCase.Response response = useCase.new Response();
                        response.responseStatus = CSPersistenceControlSystemEntryOpUseCase.Status.IN_PROGRESS;
                        RxBus.INSTANCE.send(response);
                    }

                    @Override
                    public void onSuccess(List<AttributeEntity> o) {
                        // When there is a system in the database, MaybeObserver will trigger onSuccess
                        // and it will complete.
                        // Now send successful response to Bus.
                        if (o != null) {
                            CSPersistenceGetControlSystemEntryUseCase useCase = new CSPersistenceGetControlSystemEntryUseCase();
                            CSPersistenceGetControlSystemEntryUseCase.Response response = useCase.new Response((ControlSystemEntry) o);

                            ControlSystemEntry entry;
                            entry = EntryModel.makeEntryObj(o, databaseInteractor.getDatabase().getAttrDao().getItemFromID(o.get(0).attrID));

                            response = useCase.new Response(entry);
                            response.responseStatus = CSPersistenceControlSystemEntryOpUseCase.Status.SUCCESS;
                            RxBus.INSTANCE.send(response);
                        }
                    }

                    @Override
                    public void onError(Throwable e) {
                        // Send error response to Bus
                        CSPersistenceGetControlSystemEntryUseCase useCase = new CSPersistenceGetControlSystemEntryUseCase();
                        CSPersistenceGetControlSystemEntryUseCase.Response response = useCase.new Response();
                        response.responseStatus.setStatusMessage(e.getMessage());
                        response.responseStatus = CSPersistenceControlSystemEntryOpUseCase.Status.FAILED;
                        RxBus.INSTANCE.send(response);
                    }

                    @Override
                    public void onComplete() {
                        // When there is no system in the database and the query returns no rows,
                        // MaybeObserver will complete.
                        // Now send empty response to Bus.
                        CSPersistenceGetControlSystemEntryUseCase useCase = new CSPersistenceGetControlSystemEntryUseCase();
                        ControlSystemEntry entry = new ControlSystemEntry();
                        CSPersistenceGetControlSystemEntryUseCase.Response response = useCase.new Response(entry);
                        response.responseStatus = CSPersistenceControlSystemEntryOpUseCase.Status.SUCCESS;
                        RxBus.INSTANCE.send(response);
                    }
                });
    }

    /**
     * Read all control sys.
     */
    private void getEntries() {
        databaseInteractor.getAllSystemsWithInfo()
                .subscribeOn(Schedulers.io())
                .subscribe(new MaybeObserver<List<AttributeEntity>>() {
                    @Override
                    public void onSubscribe(Disposable d) {
                        //Send in progress status
                        CSPersistenceGetControlSystemsEntriesUseCase useCase = new CSPersistenceGetControlSystemsEntriesUseCase();
                        CSPersistenceGetControlSystemsEntriesUseCase.Response response = useCase.new Response();
                        response.responseStatus = CSPersistenceControlSystemEntryOpUseCase.Status.IN_PROGRESS;
                        RxBus.INSTANCE.send(response);
                    }

                    @Override
                    public void onSuccess(List<AttributeEntity> o) {
                        // When there is a list of systems in the database, MaybeObserver will trigger
                        // onSuccess and it will complete.
                        // Now send response to Bus.
                        if (o != null) {
                            CSPersistenceGetControlSystemsEntriesUseCase useCase = new CSPersistenceGetControlSystemsEntriesUseCase();
                            CSPersistenceGetControlSystemsEntriesUseCase.Response response = null;
                            List<ControlSystemEntry> entryList = new ArrayList<>();
                            List<ItemEntity> itemList = databaseInteractor.getDatabase().getAttrDao().getAllItems();
                            // Send non-empty list back to UI
                            if (o.size() > 0) {
                                //Iterate and create a list of ControlSystemEntry obj's to return to caller
                                for (ItemEntity i : itemList) {
                                    ControlSystemEntry entry;
                                    entry = EntryModel.makeEntryObj(o, i);
                                    entryList.add(entry);
                                }
                            }
                            response = useCase.new Response(entryList);
                            response.responseStatus = CSPersistenceControlSystemEntryOpUseCase.Status.SUCCESS;
                            RxBus.INSTANCE.send(response);
                        }
                    }

                    @Override
                    public void onError(Throwable e) {
                        // send error response to Bus
                        CSPersistenceGetControlSystemsEntriesUseCase useCase = new CSPersistenceGetControlSystemsEntriesUseCase();
                        CSPersistenceGetControlSystemsEntriesUseCase.Response response = useCase.new Response();
                        response.responseStatus.setStatusMessage(e.getMessage());
                        response.responseStatus = CSPersistenceControlSystemEntryOpUseCase.Status.FAILED;
                        RxBus.INSTANCE.send(response);
                    }

                    @Override
                    public void onComplete() {
                        // When there is no list of systems in the database and the query returns no
                        // rows, MaybeObserver will complete.
                        // Now send empty response to Bus.
                        CSPersistenceGetControlSystemsEntriesUseCase useCase = new CSPersistenceGetControlSystemsEntriesUseCase();
                        List<ControlSystemEntry> entries = new ArrayList<>();
                        CSPersistenceGetControlSystemsEntriesUseCase.Response response = useCase.new Response(entries);
                        response.responseStatus = CSPersistenceControlSystemEntryOpUseCase.Status.SUCCESS;
                        RxBus.INSTANCE.send(response);
                    }
                });
    }

    /**
     * Create control sys entry.
     *
     * @param controlSystemEntry ControlSystemEntry
     */
    @VisibleForTesting
    public void createEntry(ControlSystemEntry controlSystemEntry) {
        databaseInteractor.createOrUpdateSystemEntry(controlSystemEntry)
                .subscribeOn(Schedulers.io())
                .subscribe(new MaybeObserver<List<AttributeEntity>>() {
                    @Override
                    public void onSubscribe(Disposable d) {
                        //Send in progress status
                        CSPersistenceCreateControlSystemEntryUseCase.Response response = new CSPersistenceCreateControlSystemEntryUseCase().new Response();
                        response.responseStatus = CSPersistenceControlSystemEntryOpUseCase.Status.IN_PROGRESS;
                        RxBus.INSTANCE.send(response);
                    }

                    @Override
                    public void onSuccess(List<AttributeEntity> o) {
                        // When there is a system in the database, MaybeObserver will trigger
                        // onSuccess and it will complete.
                        // Now send success response to Bus
                        // Build a list of one to send back to UI
                        if (o != null) {
                            ItemEntity itemList = databaseInteractor.getDatabase().getAttrDao().getItemFromID(o.get(0).attrID);
                            List<ControlSystemEntry> list = new ArrayList<>();
                            ControlSystemEntry entry = new ControlSystemEntry();
                            entry.setControlSystemID(String.valueOf(itemList.getId()));
                            entry.setFriendlyName(itemList.getItem_key());
                            list.add(entry);

                            // Send the use case back to UI
                            CSPersistenceCreateControlSystemEntryUseCase useCase = new CSPersistenceCreateControlSystemEntryUseCase();
                            CSPersistenceCreateControlSystemEntryUseCase.Response response = useCase.new Response(list);
                            response.responseStatus = CSPersistenceControlSystemEntryOpUseCase.Status.SUCCESS;
                            RxBus.INSTANCE.send(response);
                        }
                    }

                    @Override
                    public void onError(Throwable e) {
                        // send error response to Bus
                        CSPersistenceCreateControlSystemEntryUseCase useCase = new CSPersistenceCreateControlSystemEntryUseCase();
                        CSPersistenceCreateControlSystemEntryUseCase.Response response = useCase.new Response();
                        response.responseStatus.setStatusMessage(e.getMessage());
                        response.responseStatus = CSPersistenceControlSystemEntryOpUseCase.Status.FAILED;
                        RxBus.INSTANCE.send(response);
                    }

                    @Override
                    public void onComplete() {
                        // When system entry wasn't created and no sysId was returned for it,
                        // MaybeObserver will complete.
                        // Now send empty response to Bus.
                        CSPersistenceCreateControlSystemEntryUseCase useCase = new CSPersistenceCreateControlSystemEntryUseCase();
                        List<ControlSystemEntry> entries = new ArrayList<>();
                        CSPersistenceCreateControlSystemEntryUseCase.Response response = useCase.new Response(entries);
                        response.responseStatus.setStatusMessage(CSPersistenceCreateControlSystemEntryUseCase.Status.FAILED.toString());
                        response.responseStatus = CSPersistenceControlSystemEntryOpUseCase.Status.FAILED;
                        RxBus.INSTANCE.send(response);
                    }
                });
    }

    /**
     * Delete control sys entry.
     *
     * @param sysId Long
     */
    private void deleteEntries(Long[] sysId) {
        databaseInteractor.deleteSystem(sysId)
                .subscribeOn(Schedulers.io())
                .subscribe(new CompletableObserver() {
                    @Override
                    public void onSubscribe(Disposable d) {
                        //Send in progress status
                        CSPersistenceDeleteControlSystemEntryUseCase useCase = new CSPersistenceDeleteControlSystemEntryUseCase();
                        CSPersistenceDeleteControlSystemEntryUseCase.Response response = useCase.new Response();
                        response.responseStatus = CSPersistenceControlSystemEntryOpUseCase.Status.IN_PROGRESS;
                        RxBus.INSTANCE.send(response);
                    }

                    @Override
                    public void onError(Throwable e) {
                        // send error response to Bus
                        CSPersistenceDeleteControlSystemEntryUseCase useCase = new CSPersistenceDeleteControlSystemEntryUseCase();
                        CSPersistenceDeleteControlSystemEntryUseCase.Response response = useCase.new Response();
                        response.responseStatus.setStatusMessage(e.getMessage());
                        response.responseStatus = CSPersistenceControlSystemEntryOpUseCase.Status.FAILED;
                        RxBus.INSTANCE.send(response);
                    }

                    @Override
                    public void onComplete() {
                        // When computation completes normally.
                        // Now send success response to Bus.
                        CSPersistenceDeleteControlSystemEntryUseCase useCase = new CSPersistenceDeleteControlSystemEntryUseCase();
                        CSPersistenceDeleteControlSystemEntryUseCase.Response response = useCase.new Response();
                        response.responseStatus = CSPersistenceControlSystemEntryOpUseCase.Status.SUCCESS;
                        RxBus.INSTANCE.send(response);
                    }
                });
    }

}