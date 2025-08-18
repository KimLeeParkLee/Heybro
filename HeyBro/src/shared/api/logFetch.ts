// This file overrides the global fetch to add logging.
// Import it once in your app's entry point (e.g., App.tsx) to activate.

// Store the original fetch function
const originalFetch = globalThis.fetch;

// Create the new fetch function
globalThis.fetch = async (input: RequestInfo | URL, init?: RequestInit): Promise<Response> => {
  const request = new Request(input, init);
  
  // --- Log the request ---
  const method = request.method.toUpperCase();
  const url = request.url;
  const headers: Record<string, string> = {};
  request.headers.forEach((value, key) => {
    headers[key] = value;
  });
  
  // Note: Reading the body consumes it, so we can't easily log it here
  // without more complex cloning, which might not be necessary for basic logging.
  // We will log the body from the `init` object if available.
  console.log(`➡️ ${method} ${url}`, { headers, body: init?.body ?? null });

  try {
    // --- Make the actual request ---
    const response = await originalFetch(request);
    
    // --- Log the response ---
    // We need to clone the response to read its body, as it can only be read once.
    // const responseClone = response.clone();
    const responseHeaders: Record<string, string> = {};
    response.headers.forEach((value, key) => {
      responseHeaders[key] = value;
    });

    // Try to parse as JSON, fall back to text
    // let responseBody: any;
    // try {
    //   responseBody = await responseClone.json();
    // } catch {
    //   responseBody = await responseClone.text();
    // }

    console.log(`⬅️ ${response.status} ${url}`, { headers: responseHeaders, body: 'Body not logged to prevent double reading' });

    return response;
  } catch (error: any) {
    console.error(`❌ NETWORK ERROR: ${method} ${url}`, error);
    // Re-throw the error to not break the application flow
    throw error;
  }
};
