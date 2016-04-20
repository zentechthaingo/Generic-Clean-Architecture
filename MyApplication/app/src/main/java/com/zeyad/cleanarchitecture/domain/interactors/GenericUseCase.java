package com.zeyad.cleanarchitecture.domain.interactors;

import com.zeyad.cleanarchitecture.domain.executors.PostExecutionThread;
import com.zeyad.cleanarchitecture.domain.executors.ThreadExecutor;
import com.zeyad.cleanarchitecture.domain.models.User;
import com.zeyad.cleanarchitecture.domain.models.mapper.ModelDataMapper;
import com.zeyad.cleanarchitecture.domain.repositories.Repository;
import com.zeyad.cleanarchitecture.presentation.model.UserModel;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import javax.inject.Inject;

import rx.Observable;
// TODO: 4/19/16 remove data mapper
/**
 * This class is a general implementation of {@link BaseUseCase} that represents a use case for
 * retrieving data related to an specific {@link User}.
 */
public class GenericUseCase extends BaseUseCase {

    private final Repository repository;
    private final ModelDataMapper modelDataMapper;

    @Inject
    public GenericUseCase(Repository repository, ThreadExecutor threadExecutor,
                          PostExecutionThread postExecutionThread) {
        super(threadExecutor, postExecutionThread);
        this.repository = repository;
        this.modelDataMapper = new ModelDataMapper();
    }

    @Override
    protected Observable buildUseCaseObservable() {
        return null;
    }

    @Override
    public Observable buildUseCaseObservableList(Class presentationClass, Class domainClass, Class dataClass) {
        return repository.Collection(presentationClass, domainClass, dataClass)
                .map(collection -> modelDataMapper.transformAllToPresentation(collection, presentationClass));
    }

    @Override
    public Observable buildUseCaseObservableDetail(int itemId, Class presentationClass, Class domainClass, Class dataClass) {
        return repository.getById(itemId, presentationClass, domainClass, dataClass)
                .map(item -> modelDataMapper.transformToPresentation(item, presentationClass));
    }

    @Override
    protected Observable buildUseCaseObservablePut(Object object, Class presentationClass, Class domainClass, Class dataClass) {
        return repository.put(object, presentationClass, domainClass, dataClass)
                .map(item -> modelDataMapper.transformToPresentation(item, presentationClass));
    }

    @Override
    protected Observable buildUseCaseObservableDeleteMultiple(Collection collection, Class presentationClass,
                                                              Class domainClass, Class dataClass) {
        return repository.deleteCollection(collection, presentationClass, domainClass, dataClass);
    }

    @Override
    protected Observable buildUseCaseObservableQuery(String query, String column, Class presentationClass, Class domainClass, Class dataClass) {
        return repository.search(query, column, presentationClass, domainClass, dataClass)
                .map(collection -> modelDataMapper.transformAllToPresentation((Collection) collection, presentationClass));
    }

    private Observable filter(List<UserModel> models, String query) {
        query = query.toLowerCase();
        final List<UserModel> filteredModelList = new ArrayList<>();
        String text;
        for (UserModel model : models) {
            text = model.getFullName().toLowerCase();
            if (text.contains(query))
                filteredModelList.add(model);
        }
        return Observable.from(filteredModelList);
    }
}