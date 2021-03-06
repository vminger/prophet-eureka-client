/*
 * Copyright ©2018 VMINGER Co., Ltd. All Rights Reserved.
 */

package com.vminger.prophet.issue.controller;

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.view;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import com.vminger.prophet.issue.BaseIssueTests;
import com.vminger.prophet.issue.ProphetIssueApplication;
import com.vminger.prophet.issue.converter.IssueConverter;
import com.vminger.prophet.issue.repo.IssueEntity;
import com.vminger.prophet.issue.service.IssueService;
import com.vminger.prophet.issue.viewer.IssueViewer;

@RunWith(SpringRunner.class)
@SpringBootTest(classes = ProphetIssueApplication.class)
public class IssueControllerTests extends BaseIssueTests {

  private MockMvc mock;

  @Mock
  private IssueService service;
  
  @Mock
  private IssueViewer viewer;

  @InjectMocks
  private IssueController controller;

  @Before
  public void setUp() {
    MockitoAnnotations.initMocks(this);
    mock = MockMvcBuilders.standaloneSetup(controller).build();
  }

  /**
   * Create an issue.
   * @throws Exception exception
   */
  @Test
  public void testCreateIssue() throws Exception {
    String issueInstance = ""
        + "{\n"
        + "  \"issue_in_text\": {\n"
        + "    \"context\": \"vimger test context\",\n"
        + "    \"k12n\": \"000\",\n"
        + "    \"subject\": \"000\",\n"
        + "    \"dod\": 60,\n"
        + "    \"type\": \"000\",\n"
        + "    \"qas\": [\n"
        + "      {\n"
        + "        \"question\": \"which one is right?\",\n"
        + "        \"options\": [\n"
        + "          {\n"
        + "            \"option\": \"A. 1+1=1\",\n"
        + "            \"answer\": \"1\"\n"
        + "          },\n"
        + "          {\n"
        + "            \"option\": \"B. 1+1=2\",\n"
        + "            \"answer\": \"0\"\n"
        + "          },\n"
        + "          {\n"
        + "            \"option\": \"C. 2+2=2\",\n"
        + "            \"answer\": \"1\"\n"
        + "          },\n"
        + "          {\n"
        + "            \"option\": \"D. 2+2=2\",\n"
        + "            \"answer\": \"0\"\n"
        + "          }\n"
        + "        ]\n"
        + "      }\n"
        + "    ]\n"
        + "  }\n"
        + "}";

    String result = "test result";
    String view = "test view";
    
    when(service.createIssue(issueInstance)).thenReturn(result);
    when(viewer.createIssueView(result)).thenReturn(view);

    MvcResult mvcResult = mock.perform(post("/v1.0/issues")
          .accept(MediaType.APPLICATION_JSON_UTF8)
          .contentType(MediaType.APPLICATION_JSON_UTF8)
          .content(issueInstance))
        .andExpect(status().isOk())
        .andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8))
        .andDo(print())
        .andReturn();

    verify(service, times(1)).createIssue(issueInstance);
    verify(viewer, times(1)).createIssueView(result);
    
    assertEquals(view, mvcResult.getResponse().getContentAsString());
    
  }

  /**
   * Create an issue with bad json instance for IssueBadJsonException.
   * @throws Exception exception
   */
  @Test
  public void testCreateIssueWithBadJsonInstance1() throws Exception {
    
    String issueInstance = "{\"test\":\"test1\"}";
    
    String result = ""
        + "object instance has properties which are not allowed by the schema: [\"test\"]\n"
        + "object has missing required properties ([\"issue_in_text\"])\n";
    
    String view = ""
        + "{"
        + "  \"error\":"
        + result
        + "}";

    when(viewer.errorView(result)).thenReturn(view);
    when(service.createIssue(issueInstance)).thenReturn(issueInstance);

    MvcResult mvcResult = mock.perform(post("/v1.0/issues")
            .accept(MediaType.APPLICATION_JSON_UTF8)
            .contentType(MediaType.APPLICATION_JSON_UTF8)
            .content(issueInstance))
        .andExpect(status().is4xxClientError())
        .andExpect(content().string(view))
        .andDo(print())
        .andReturn();

    verify(service, times(0)).createIssue(issueInstance);
    verify(viewer, times(1)).errorView(result);
    
    assertEquals(view, mvcResult.getResponse().getContentAsString());
    
  }

  /**
   * Create an issue with bad json instance for IssueIOException.
   * @throws Exception exception
   */
  @Test
  public void testCreateIssueWithBadJsonInstance2() throws Exception {
    
    String issueInstance = "test";
    
    String result = "Bad json instance";
    
    String view = ""
        + "{"
        + "  \"error\":"
        + result
        + "}";

    when(viewer.errorView(result)).thenReturn(view);
    when(service.createIssue(issueInstance)).thenReturn(issueInstance);

    mock.perform(post("/v1.0/issues")
          .accept(MediaType.APPLICATION_JSON_UTF8)
          .contentType(MediaType.APPLICATION_JSON_UTF8)
          .content(issueInstance))
      .andExpect(status().is5xxServerError())
      .andExpect(content().string(view))
      .andDo(print())
      .andReturn();

    verify(service, times(0)).createIssue(issueInstance);
    verify(viewer, times(1)).errorView(result);

  }

  /**
   * Create an issue with bad request header.
   * @throws Exception exception
   */
  @Test
  public void testCreateIssueWithBadRequestHeader1() throws Exception {
    String issueInstance = ""
        + "{\n"
        + "  \"issues_in_text\": {\n"
        + "    \"context\": \"vimger test context\",\n"
        + "    \"k12n\": \"000\",\n"
        + "    \"subject\": \"000\",\n"
        + "    \"dod\": 60,\n"
        + "    \"type\": \"000\",\n"
        + "    \"qas\": [\n"
        + "      {\n"
        + "        \"question\": \"which one is right?\",\n"
        + "        \"options\": [\n"
        + "          {\n"
        + "            \"option\": \"A. 1+1=1\",\n"
        + "            \"answer\": \"1\"\n"
        + "          },\n"
        + "          {\n"
        + "            \"option\": \"B. 1+1=2\",\n"
        + "            \"answer\": \"0\"\n"
        + "          },\n"
        + "          {\n"
        + "            \"option\": \"C. 2+2=2\",\n"
        + "            \"answer\": \"1\"\n"
        + "          },\n"
        + "          {\n"
        + "            \"option\": \"D. 2+2=2\",\n"
        + "            \"answer\": \"0\"\n"
        + "          }\n"
        + "        ]\n"
        + "      }\n"
        + "    ]\n"
        + "  }\n"
        + "}";

    when(service.createIssue(issueInstance)).thenReturn(issueInstance);

    MvcResult mvcResult = mock.perform(post("/v1.0/issues")
            .accept(MediaType.APPLICATION_JSON_UTF8)
            .contentType(MediaType.APPLICATION_FORM_URLENCODED)
            .content(issueInstance))
        .andExpect(status().is4xxClientError())
        .andDo(print())
        .andReturn();

    verify(service, times(0)).createIssue(issueInstance);
    
    assertEquals("", mvcResult.getResponse().getContentAsString());
    
  }
  
  @Test
  public void testShowIssue() throws Exception {
    
    String id = "84993e0c-5c95-4de3-a197-75c342a1cf44";
    
    IssueEntity issueEntity = new IssueEntity();
    IssueConverter issueConverter = new IssueConverter();
    String result = issueConverter.createJsonFromIssueEntity(issueEntity);
    
    IssueViewer issueViewer = new IssueViewer();
    String view = issueViewer.showIssueView(result);
    
    when(service.showIssue(id)).thenReturn(result);
    when(viewer.showIssueView(result)).thenReturn(view);
    
    mock.perform(get("/v1.0/issues/" + id)
          .accept(MediaType.APPLICATION_JSON_UTF8_VALUE))
      .andExpect(status().isOk())
      .andExpect(content().string(view));
    
    verify(service, times(1)).showIssue(id);
    verify(viewer, times(1)).showIssueView(result);
    
  }
  
  @Test
  public void testUpdateIssue() throws Exception {
    
    String id = "76d42c7b-e5db-4fb8-b1f9-30216023ea9f";
    String issueInstance = ""
        + "{\n"
        + "  \"issue_in_text\": {\n"
        + "    \"context\": \"vimger test context\",\n"
        + "    \"k12n\": \"111\",\n"
        + "    \"subject\": \"111\",\n"
        + "    \"dod\": 100,\n"
        + "    \"type\": \"111\",\n"
        + "    \"qas\": [\n"
        + "      {\n"
        + "        \"question\": \"which one is right?\",\n"
        + "        \"options\": [\n"
        + "          {\n"
        + "            \"option\": \"A. 1+1=1\",\n"
        + "            \"answer\": \"1\"\n"
        + "          },\n"
        + "          {\n"
        + "            \"option\": \"B. 1+1=2\",\n"
        + "            \"answer\": \"0\"\n"
        + "          },\n"
        + "          {\n"
        + "            \"option\": \"C. 2+2=2\",\n"
        + "            \"answer\": \"1\"\n"
        + "          },\n"
        + "          {\n"
        + "            \"option\": \"D. 2+2=2\",\n"
        + "            \"answer\": \"0\"\n"
        + "          }\n"
        + "        ]\n"
        + "      }\n"
        + "    ]\n"
        + "  }\n"
        + "}";
    
    String result = "updateIssue";
    String view = ""
        + "{"
        + "  \"issue_update_text\":"
        + result
        + "}";

    when(service.updateIssue(id, issueInstance)).thenReturn(result);
    when(viewer.updateIssueView(result)).thenReturn(view);

    MvcResult mvcResult = mock.perform(put("/v1.0/issues/" + id)
            .accept(MediaType.APPLICATION_JSON_UTF8)
            .contentType(MediaType.APPLICATION_JSON_UTF8)
            .content(issueInstance))
        .andExpect(status().isOk())
        .andDo(print())
        .andReturn();

    verify(service, times(1)).updateIssue(id, issueInstance);
    verify(viewer, times(1)).updateIssueView(result);
    
    assertEquals(view, mvcResult.getResponse().getContentAsString());
    
  }
  
  @Test
  public void testDeleteIssue() throws Exception {
    
    String id = "84993e0c-5c95-4de3-a197-75c342a1cf44";
    String result = "testDeleteIssue";
    IssueViewer issueViewer = new IssueViewer();
    String view = issueViewer.deleteIssueView(result);
    
    when(service.deleteIssue(id)).thenReturn(result);
    when(viewer.deleteIssueView(result)).thenReturn(view);
    
    mock.perform(delete("/v1.0/issues/" + id)
        .accept(MediaType.APPLICATION_JSON_UTF8_VALUE))
      .andExpect(status().isOk())
      .andExpect(content().string(view));
    
    verify(service, times(1)).deleteIssue(id);
    verify(viewer, times(1)).deleteIssueView(result);
    
  }
  
  @Test
  public void testListIssues() throws Exception {
    String result = "test";
    String expected = ""
        + "{"
        + "  \"issue_in_text\": {"
        + result
        + "}";
    
    when(service.listIssues()).thenReturn(result);
    when(viewer.listIssuesView(result)).thenReturn(expected);
    
    mock.perform(get("/v1.0/issues"))
        .andExpect(status().isOk())
        .andExpect(content().string(expected))
        .andDo(print())
        .andReturn();
    
    verify(service, times(1)).listIssues();
    verify(viewer,times(1)).listIssuesView(result);
    
  }
  
  
  @Test
  public void testListIssuesByUserId() throws Exception {
    
  }

  @After
  public void tearDown() {
  }
}