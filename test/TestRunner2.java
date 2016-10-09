import org.junit.runner.JUnitCore;
import org.junit.runner.Result;
import org.junit.runner.notification.Failure;

public class TestRunner2 {
   public static void main(String[] args) {
      Result result = JUnitCore.runClasses(TestAssertions.class);
		
      for (Failure failure : result.getFailures()) {
         System.out.println(failure);
         //System.out.println(failure.toString());
      }
		
      System.out.println(result.wasSuccessful());
   }
} 

/*output
run:
true
BUILD SUCCESSFUL (total time: 0 seconds)

*/