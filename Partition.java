String otp = null;

ExecutorService executor = Executors.newSingleThreadExecutor();
Future<String> future = executor.submit(() -> 
    JOptionPane.showInputDialog("Enter the OTP received on your email or phone (You have 5 minutes):")
);

try {
    otp = future.get(5, TimeUnit.MINUTES); // Wait max 5 minutes
    if (otp == null || otp.isEmpty()) {
        System.out.println("No OTP entered. Exiting...");
        return;
    }
    System.out.println("OTP entered: " + otp);
} catch (TimeoutException e) {
    System.out.println("OTP entry timed out!");
    future.cancel(true);
    return;
} catch (Exception e) {
    e.printStackTrace();
    return;
} finally {
    executor.shutdownNow();
}
