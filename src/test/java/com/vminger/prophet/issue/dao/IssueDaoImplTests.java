/*
 * Copyright ©2018 VMINGER Co., Ltd. All Rights Reserved.
 */

package com.vminger.prophet.issue.dao;

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.LinkedList;
import java.util.List;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.test.context.junit4.SpringRunner;

import com.vminger.prophet.issue.ProphetIssueApplication;
import com.vminger.prophet.issue.repo.drivers.mongodb.dao.IssueDaoImplMongo;
import com.vminger.prophet.issue.repo.drivers.mongodb.entity.IssueEntityMongo;

@RunWith(SpringRunner.class)
@SpringBootTest(classes = ProphetIssueApplication.class)
public class IssueDaoImplTests {

  @Mock
  MongoTemplate template;
  
  @InjectMocks
  IssueDaoImplMongo issueDaoImpl;
  
  @Before
  public void setUp() {
    MockitoAnnotations.initMocks(this);
  }
  
  @Test
  public void testInsert() throws Exception {
    IssueEntityMongo issueEntity = new IssueEntityMongo();
    issueDaoImpl.insert(issueEntity);
    verify(template, times(1)).insert(issueEntity);
  }
  
  @Test
  public void testInsertAll() throws Exception {
    List<IssueEntityMongo> issueEntities = new LinkedList<IssueEntityMongo>();
    IssueEntityMongo issueEntity = new IssueEntityMongo();
    issueEntities.add(issueEntity);
    issueDaoImpl.insertAll(issueEntities);
    verify(template, times(1)).insertAll(issueEntities);
  }
  
  @Test
  public void testDelete() throws Exception {
    IssueEntityMongo issueEntity = new IssueEntityMongo();
    issueDaoImpl.delete(issueEntity);
    verify(template, times(1)).remove(issueEntity);
  }
  
  @Test
  public void testDeleteById() throws Exception {
    String contextId = "b15de281-5868-4992-b918-63d582e69ecb";
    issueDaoImpl.deleteById(contextId);
  }
  
  @Test
  public void testFindById() throws Exception {
    String contextId = "b15de281-5868-4992-b918-63d582e69ecb";
    IssueEntityMongo issueEntity = new IssueEntityMongo();
    when(template.findById(contextId, IssueEntityMongo.class)).thenReturn(issueEntity);
    
    IssueEntityMongo retEntity = issueDaoImpl.findById(contextId);
    assertEquals(retEntity, issueEntity);
  }
  
  @Test
  public void testFindByUserId() throws Exception {
    String userId = "85fd2f86-be41-4bd7-b34b-7139e90e1ad1";
    
    List<IssueEntityMongo> issueEntities = issueDaoImpl.findByUserId(userId);
    
    assertEquals(issueEntities, null);
  }
  
  @Test
  public void testListAllIssues() throws Exception {
    List<IssueEntityMongo> expected = new LinkedList<IssueEntityMongo>();
    IssueEntityMongo issueEntity = new IssueEntityMongo();
    issueEntity.setContextId("f597e7aa-bae8-407b-a77c-9dd0a09d7a72");
    issueEntity.setContext("test context");
    expected.add(issueEntity);
    
    when(template.findAll(IssueEntityMongo.class)).thenReturn(expected);
    
    List<IssueEntityMongo> actual = issueDaoImpl.listAllIssues();
    
    assertEquals(expected, actual);
  }
  
  @After
  public void tearDown() {
  }
  
}
