package com.zeyad.cleanarchitecture.domain.interactors;

import com.zeyad.cleanarchitecture.domain.executors.PostExecutionThread;
import com.zeyad.cleanarchitecture.domain.executors.ThreadExecutor;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.HashMap;

import io.realm.RealmQuery;
import rx.Observable;
import rx.observers.TestSubscriber;
import rx.schedulers.TestScheduler;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.core.Is.is;
import static org.mockito.BDDMockito.given;

public class BaseUseCaseTest {

    private BaseUseCaseTestClass useCase;
    @Mock
    private ThreadExecutor mockThreadExecutor;
    @Mock
    private PostExecutionThread mockPostExecutionThread;

    @Before
    public void setUp() {
        MockitoAnnotations.initMocks(this);
        useCase = new BaseUseCaseTestClass(mockThreadExecutor, mockPostExecutionThread);
    }

    @Test
    @SuppressWarnings("unchecked")
    public void testBuildUseCaseObservableReturnCorrectResult() {
        TestSubscriber<Integer> testSubscriber = new TestSubscriber<>();
        TestScheduler testScheduler = new TestScheduler();
        given(mockPostExecutionThread.getScheduler()).willReturn(testScheduler);
        useCase.execute(testSubscriber);
        assertThat(testSubscriber.getOnNextEvents().size(), is(0));
    }

    @Test
    public void testSubscriptionWhenExecutingUseCase() {
        TestSubscriber<Integer> testSubscriber = new TestSubscriber<>();
        useCase.execute(testSubscriber);
        useCase.unsubscribe();
        assertThat(testSubscriber.isUnsubscribed(), is(true));
    }

    @Test
    public void testBuildUseCaseObservableDynamicList() throws Exception {

    }

    @Test
    public void testBuildUseCaseObservableDynamicObjectById() throws Exception {

    }

    @Test
    public void testBuildUseCaseObservablePut() throws Exception {

    }

    @Test
    public void testBuildUseCaseObservableDynamicPostList() throws Exception {

    }

    @Test
    public void testBuildUseCaseObservableDeleteMultiple() throws Exception {

    }

    @Test
    public void testBuildUseCaseObservableQuery() throws Exception {

    }

    @Test
    public void testBuildUseCaseObservableRealmQuery() throws Exception {

    }

    @Test
    public void testExecuteList() throws Exception {

    }

    @Test
    public void testExecuteGetObject() throws Exception {

    }

    @Test
    public void testExecuteDynamicPutObject() throws Exception {

    }

    @Test
    public void testExecuteDynamicPostList() throws Exception {

    }

    @Test
    public void testExecuteSearch() throws Exception {

    }

    @Test
    public void testExecuteSearch1() throws Exception {

    }

    @Test
    public void testExecuteDeleteCollection() throws Exception {

    }

    private static class BaseUseCaseTestClass extends BaseUseCase {

        protected BaseUseCaseTestClass(ThreadExecutor threadExecutor,
                                       PostExecutionThread postExecutionThread) {
            super(threadExecutor, postExecutionThread);
        }

        @Override
        protected Observable buildUseCaseObservableDynamicList(String url, Class presentationClass, Class domainClass, Class dataClass, boolean persist) {
            return null;
        }

        @Override
        protected Observable buildUseCaseObservableDynamicObjectById(String url, String idColumnName, int itemId, Class presentationClass, Class domainClass, Class dataClass, boolean persist) {
            return null;
        }

        @Override
        protected Observable buildUseCaseObservablePut(String url, HashMap<String, Object> keyValuePairs, Class presentationClass, Class domainClass, Class dataClass, boolean persist) {
            return null;
        }

        @Override
        protected Observable buildUseCaseObservableDynamicPostList(String url, HashMap<String, Object> keyValuePairs, Class presentationClass, Class domainClass, Class dataClass, boolean persist) {
            return null;
        }

        @Override
        protected Observable buildUseCaseObservableDeleteMultiple(String url, HashMap<String, Object> keyValuePairs, Class domainClass, Class dataClass, boolean persist) {
            return null;
        }

        @Override
        protected Observable buildUseCaseObservableQuery(String query, String column, Class presentationClass, Class domainClass, Class dataClass) {
            return null;
        }

        @Override
        protected Observable buildUseCaseObservableRealmQuery(RealmQuery realmQuery, Class presentationClass, Class domainClass) {
//            super.executeSearch(realmQuery, domainClass);
            return null;
        }

//        @Override
//        public void execute(Subscriber UseCaseSubscriber) {
//            super.execute(UseCaseSubscriber);
//        }
    }
}