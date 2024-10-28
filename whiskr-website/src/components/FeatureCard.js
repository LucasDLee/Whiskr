export const FeatureCard = (props) => (
  <div className="featureCard">
    <div className="featureCardIcon">
      {props.icon}
    </div>

    <div className="featureCardTitle">{props.title}</div>

    <div className="featureCardLine" />

    <div className="featureCardDescription">{props.children}</div>
  </div>
);
