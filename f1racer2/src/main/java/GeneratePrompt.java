

import com.theokanning.openai.service.OpenAiService;
import com.theokanning.openai.completion.CompletionRequest;
import javax.servlet.*;
import javax.servlet.http.*;
import javax.servlet.annotation.WebServlet;
import java.io.IOException;

public class GeneratePrompt {

    private static final String OPENAI_API_KEY = "";

  //the following 8 lines of code were provided by chatpt
    static String generatePrompt(int difficulty, String topic) {
    	String constructPrompt = constructPrompt(difficulty, topic);
        OpenAiService service = new OpenAiService(OPENAI_API_KEY);
        CompletionRequest completionRequest = CompletionRequest.builder()
        	    .prompt(constructPrompt)
        	    .maxTokens(200) // Adjust as needed
        	    .model("text-davinci-003")
        	    .build();


        String completionText = service.createCompletion(completionRequest).getChoices().get(0).getText().trim();
        return completionText;

    	
    }

    static String constructPrompt(int difficulty, String topic) {
        if (topic.length() == 0) {
            topic = "a random PG13 topic"; // Default topic
        }

        switch (difficulty) {
            case 1:
                return "Write a 40-50 word paragraph on " + topic + ". Suitable for a 2nd grader, with no complicated words and punctuation besides periods.";
            case 2:
                return "Write a 50-60 word paragraph on " + topic + ". Suitable for a 4th grader, with no complicated words but slight punctuation allowed.";
            case 3:
                return "Write a 50-60 word paragraph on " + topic + ". Suitable for a 6th grader.";
            case 4:
                return "Write a 50-60 word paragraph on " + topic + ". Suitable for a 6th grader, with at least 5 punctuations that aren't periods (e.g., commas, dashes).";
            case 5:
                return "Write a 60-70 word paragraph on " + topic + ". Suitable for an 8th grader.";
            case 6:
                return "Write a 60-70 word paragraph on " + topic + ". Suitable for an 8th grader, with at least 3 SAT words of 7 characters or more.";
            case 7:
                return "Write a 60-70 word paragraph on " + topic + ". Suitable for a 10th grader, with at least 5 SAT words of 7 characters or more and multiple punctuations that aren't periods.";
            case 8:
                return "Write a 70-80 word paragraph on " + topic + ". In semi-academic language, include high-level language in at least 10 words.";
            case 9:
                return "Write an 80-90 word paragraph on " + topic + ". In academic language, include high-level language in at least 10 words and multiple punctuations that aren't periods.";
            case 10:
                return "Write a 90-100 word paragraph on " + topic + ". In academic language, include high-level language in at least 10 words, multiple punctuations that aren't periods, and something in quotation marks.";
            default:
                return "Invalid difficulty level.";
        }
    }
}


