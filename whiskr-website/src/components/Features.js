import { FeatureCard } from "./FeatureCard";

export const Features = () => {

  const featuresData = [
    {
      href: "https://youtu.be/tPzHgD19CP4",
      icon: (
        <svg xmlns="http://www.w3.org/2000/svg" viewBox="0 0 26 26">
          <polygon className="play-btn__svg" points="9.33 6.69 9.33 19.39 19.3 13.04 9.33 6.69" fill="white" stroke="none" />
          <path className="play-btn__svg" d="M26,13A13,13,0,1,1,13,0,13,13,0,0,1,26,13ZM13,2.18A10.89,10.89,0,1,0,23.84,13.06,10.89,10.89,0,0,0,13,2.18Z" fill="white" stroke="none" />
        </svg>
      ),
      title: "Project Pitch",
      description: "See our project pitch which introduces our problem statement and solution, as well as the features we plan to implement!",
    },
    {
      href: "https://youtu.be/K6d81liuEe0",
      icon: (
        <svg xmlns="http://www.w3.org/2000/svg" viewBox="0 0 26 26">
          <polygon className="play-btn__svg" points="9.33 6.69 9.33 19.39 19.3 13.04 9.33 6.69" fill="white" stroke="none" />
          <path className="play-btn__svg" d="M26,13A13,13,0,1,1,13,0,13,13,0,0,1,26,13ZM13,2.18A10.89,10.89,0,1,0,23.84,13.06,10.89,10.89,0,0,0,13,2.18Z" fill="white" stroke="none" />
        </svg>
      ),
      title: "Show and Tell 1",
      description: "In our first project update, we highlight some of the work we've completed so far. This includes a Figma mockup showcasing our user interface, a Model View Viewmodel visualizing our data flow, and a small demonstration of the app to name a few!",
    }
    // Add more feature objects here as needed
  ];
  

  return (
    <section className="featuresSection">
      <div className="featureSeparator">
        <div className="featureHeader">Resources</div>
        <div className="featureSubheader">Learn More About the Progress of Whiskr</div>
        <div className="featureDescription">We've recorded our progress to demonstrate the full lifecycle of our groundbreaking cat adoption application.</div>
      </div>
      <div className="featureGrid">
        {featuresData.map((feature, key) => {
          const content = (
            <FeatureCard icon={feature.icon} title={feature.title}>
              {feature.description}
            </FeatureCard>
          );

          return feature.href ? (
            <a key={feature.id} href={feature.href} target="_blank" rel="noreferrer noopener">
              {content}
            </a>
          ) : (
            <div key={key}>{content}</div>
          );
        })}
      </div>
    </section>
  );
};
