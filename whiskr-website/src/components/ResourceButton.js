export const ResourceButton = (props) => {
  const handleClick = (event) => {
    if (!props.link) {
      event.preventDefault();
      alert("Sorry, this resource isn't available yet.");
    }
  };

  return (
    <a href={props.link} className="resourceButton" target="_blank" rel="noreferrer" download={props.download} onClick={handleClick}>
      <img src={props.icon} alt={props.icon} className="resourceIcon" />
      <div>
        <div className="resourceTitle">{ props.title }</div>
        <div className="resourceDescription">{ props.description }</div>
      </div>
    </a>
  )
}