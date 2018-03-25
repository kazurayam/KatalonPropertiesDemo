import static com.kms.katalon.core.checkpoint.CheckpointFactory.findCheckpoint
import static com.kms.katalon.core.testcase.TestCaseFactory.findTestCase
import static com.kms.katalon.core.testdata.TestDataFactory.findTestData
import static com.kms.katalon.core.testobject.ObjectRepository.findTestObject

import com.kms.katalon.core.checkpoint.Checkpoint as Checkpoint
import com.kms.katalon.core.model.FailureHandling as FailureHandling
import com.kms.katalon.core.testcase.TestCase as TestCase
import com.kms.katalon.core.testdata.TestData as TestData
import com.kms.katalon.core.testobject.TestObject as TestObject

import com.kms.katalon.core.webservice.keyword.WSBuiltInKeywords as WS
import com.kms.katalon.core.webui.keyword.WebUiBuiltInKeywords as WebUI
import com.kms.katalon.core.mobile.keyword.MobileBuiltInKeywords as Mobile

import internal.GlobalVariable as GlobalVariable

import com.kms.katalon.core.annotation.BeforeTestCase
import com.kms.katalon.core.annotation.BeforeTestSuite
import com.kms.katalon.core.annotation.AfterTestCase
import com.kms.katalon.core.annotation.AfterTestSuite
import com.kms.katalon.core.context.TestCaseContext
import com.kms.katalon.core.context.TestSuiteContext

import java.nio.file.Path
import java.nio.file.Paths

import com.kazurayam.KatalonProperties

class TL_Run {

    /**
     * Executes before every test suite starts.
     * @param testSuiteContext: related information of the executed test suite.
     */
    @BeforeTestSuite
    def sampleBeforeTestSuite(TestSuiteContext testSuiteContext) {
        WebUI.comment(">>> GlobalVarialbe.hostname default value: ${GlobalVariable.hostname}");
        KatalonProperties props = new KatalonProperties()
        String priorityHostname = props.getProperty('GlobalVariable.hostname')
        if (priorityHostname != null) {
            GlobalVariable.hostname = priorityHostname;
            WebUI.comment(">>> GlobalVariable.hostname new value: ${priorityHostname}");
        } else {
            WebUI.comment(">>> GlobalVariable.hostname stays unchanged");
        }
    }

}