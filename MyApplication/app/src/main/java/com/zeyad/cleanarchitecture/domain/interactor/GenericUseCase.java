package com.zeyad.cleanarchitecture.domain.interactor;

import com.zeyad.cleanarchitecture.domain.executors.PostExecutionThread;
import com.zeyad.cleanarchitecture.domain.executors.ThreadExecutor;
import com.zeyad.cleanarchitecture.domain.models.User;
import com.zeyad.cleanarchitecture.domain.repositories.Repository;
import com.zeyad.cleanarchitecture.presentation.model.UserModel;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import javax.inject.Inject;

import rx.Observable;

/**
 * This class is a general implementation of {@link BaseUseCase} that represents a use case for
 * retrieving data related to an specific {@link User}.
 */
// FIXME: 3/28/16 Test!
public class GenericUseCase extends BaseUseCase {

    private final Repository repository;

    @Inject
    public GenericUseCase(Repository repository, ThreadExecutor threadExecutor,
                          PostExecutionThread postExecutionThread) {
        super(threadExecutor, postExecutionThread);
        this.repository = repository;
    }

    @Override
    protected Observable buildUseCaseObservable() {
        return null;
    }

    @Override
    public Observable buildUseCaseObservableList(Class presentationClass, Class domainClass, Class dataClass) {
        return repository.Collection(presentationClass, domainClass, dataClass);
    }

    @Override
    public Observable buildUseCaseObservableDetail(int itemId, Class presentationClass, Class domainClass, Class dataClass) {
        return repository.getById(itemId, presentationClass, domainClass, dataClass);
    }

    @Override
    protected Observable buildUseCaseObservablePut(Object object, Class presentationClass, Class domainClass, Class dataClass) {
        return repository.put(object, presentationClass, domainClass, dataClass);
    }

    @Override
    protected Observable buildUseCaseObservableDelete(Object object, Class presentationClass, Class domainClass, Class dataClass) {
        return repository.delete(object, domainClass, dataClass);
    }

    @Override
    protected Observable buildUseCaseObservableDelete(long itemId, Class presentationClass, Class domainClass, Class dataClass) {
        return repository.delete(itemId, domainClass, dataClass);
    }

    @Override
    protected Observable buildUseCaseObservableDeleteMultiple(Collection collection, Class presentationClass,
                                                              Class domainClass, Class dataClass) {
        return repository.deleteCollection(collection, presentationClass, domainClass, dataClass);
    }

    @Override
    protected Observable buildUseCaseObservableQuery(String query, Class presentationClass, Class domainClass, Class dataClass) {
        return repository.search(query, presentationClass, domainClass, dataClass);
    }

    private List<UserModel> filter(List<UserModel> models, String query) {
        query = query.toLowerCase();
        final List<UserModel> filteredModelList = new ArrayList<>();
        for (UserModel model : models) {
            final String text = model.getFullName().toLowerCase();
            if (text.contains(query))
                filteredModelList.add(model);
        }
        return filteredModelList;
    }
}