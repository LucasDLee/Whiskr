import { FeatureCard } from "./FeatureCard";

export const Features = () => {

  return (
    <div class="featuresSection">
      <div class="featureSeparator">
        <div class="featureHeader">Resources</div>
        <div class="featureSubheader">Learn More About the Progress of Whiskr</div>
        <div class="featureDescription">We've recorded our progress to demonstrate the full lifecycle of our groundbreaking cat adoption application.</div>
      </div>
      <div class="featureGrid">
        <a href="https://drive.google.com/file/d/18I_Ak6GJKR6-1bF1CcT-RvQJ0V3owQ2b/view" target="_blank" rel="noreferrer noopener">
            <FeatureCard
              icon={(
                <svg xmlns="http://www.w3.org/2000/svg" viewBox="0 0 26 26">
                  <polygon className="play-btn__svg" points="9.33 6.69 9.33 19.39 19.3 13.04 9.33 6.69" fill="white" stroke="none" />
                  <path className="play-btn__svg" d="M26,13A13,13,0,1,1,13,0,13,13,0,0,1,26,13ZM13,2.18A10.89,10.89,0,1,0,23.84,13.06,10.89,10.89,0,0,0,13,2.18Z" fill="white" stroke="none" />
                </svg>
              )}
              title={"Project Pitch"}
            >
              {"See our project pitch which introduces our problem statement and solution, as well as the features we plan to implement!"}
            </FeatureCard>
          </a>
      </div>
    </div>
  );
};
